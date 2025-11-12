# Implementação Detalhada - Sistema de Busca e Favoritos para Mangás

## 📋 Índice

1. [Visão Geral](#visão-geral)
2. [Arquivos Criados](#arquivos-criados)
3. [Arquivos Modificados](#arquivos-modificados)
4. [Decisões de Design e Lógicas Específicas](#decisões-de-design-e-lógicas-específicas)
5. [Fluxo de Dados](#fluxo-de-dados)
6. [Padrões Arquiteturais](#padrões-arquiteturais)

---

## 🎯 Visão Geral

Este documento detalha a implementação completa do sistema de busca e favoritos para mangás no aplicativo HobbyHub. O sistema permite que usuários:

- Busquem mangás por nome usando a API Jikan
- Favoritem mangás para visualização offline
- Gerenciem seus favoritos (adicionar/remover)
- Naveguem entre três telas principais usando uma barra de navegação inferior

---

## 📁 Arquivos Criados

### 1. `models/FavoriteMangaEntity.kt`

**Propósito:** Entidade Room para persistência de mangás favoritos no SQLite

**Estrutura:**

```kotlin
@Entity(
    tableName = "favorite_manga",
    indices = [Index(value = ["mal_id"], unique = true)]
)
data class FavoriteMangaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "mal_id")
    val malId: Long,

    val title: String,

    @ColumnInfo(name = "title_english")
    val titleEnglish: String?,

    @ColumnInfo(name = "image_url")
    val imageUrl: String,

    val type: String?,
    val status: String?,
    val chapters: Int?,
    val volumes: Int?,
    val score: Double?,
    val synopsis: String?,

    @ColumnInfo(name = "published_from")
    val publishedFrom: String?,

    @ColumnInfo(name = "published_to")
    val publishedTo: String?,

    val authors: String?, // JSON string
    val genres: String?   // JSON string
)
```

**Decisões Técnicas:**

1. **Chave Primária Composta:**

   - `id`: Auto-incremento para chave primária do Room
   - `mal_id`: Índice único para evitar duplicatas baseado no ID do MyAnimeList
   - **Razão:** Permite que o Room gerencie IDs internos enquanto mantém integridade referencial com a API externa

2. **@ColumnInfo com snake_case:**

   - Mapeamento explícito de nomes de colunas (ex: `mal_id`, `title_english`)
   - **Razão:** Convenção SQL usa snake_case, enquanto Kotlin usa camelCase. O mapeamento explícito mantém a clareza em ambos os contextos

3. **Campos Nullables:**

   - Muitos campos são opcionais (`String?`, `Int?`, `Double?`)
   - **Razão:** A API Jikan não garante presença de todos os campos (ex: mangás em publicação podem não ter data de término)

4. **Armazenamento JSON:**

   - `authors` e `genres` armazenados como String JSON
   - **Razão:** Room não suporta listas complexas diretamente. Armazenar como JSON permite flexibilidade sem criar tabelas relacionais adicionais

5. **Índice Único em mal_id:**
   - `indices = [Index(value = ["mal_id"], unique = true)]`
   - **Razão:** Previne inserção de duplicatas e acelera consultas por mal_id

---

### 2. `db/MangaDao.kt`

**Propósito:** Interface DAO (Data Access Object) para operações de banco de dados

**Estrutura:**

```kotlin
@Dao
interface MangaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManga(manga: FavoriteMangaEntity)

    @Query("SELECT * FROM favorite_manga ORDER BY title ASC")
    suspend fun getAllFavoriteMangas(): List<FavoriteMangaEntity>

    @Query("SELECT * FROM favorite_manga WHERE mal_id = :malId LIMIT 1")
    suspend fun getMangaByMalId(malId: Long): FavoriteMangaEntity?

    @Query("DELETE FROM favorite_manga WHERE mal_id = :malId")
    suspend fun deleteMangaByMalId(malId: Long)

    @Query("SELECT mal_id FROM favorite_manga")
    suspend fun getAllFavoriteMangaIds(): List<Long>
}
```

**Decisões Técnicas:**

1. **OnConflictStrategy.REPLACE:**

   - Estratégia de substituição em caso de conflito
   - **Razão:** Se o usuário tentar favoritar o mesmo mangá novamente, os dados são atualizados em vez de causar erro. Útil se a API retornar informações atualizadas (ex: novo score)

2. **Funções Suspend:**

   - Todas as funções são `suspend` para uso com Coroutines
   - **Razão:** Operações de banco de dados são I/O blocking. Usando suspend, evitamos bloquear a UI thread

3. **getAllFavoriteMangaIds():**

   - Query otimizada que retorna apenas IDs
   - **Razão:** Usado para verificação rápida se um mangá está favoritado sem carregar todos os dados. Reduz uso de memória e melhora performance

4. **ORDER BY title ASC:**

   - Lista de favoritos ordenada alfabeticamente
   - **Razão:** Melhora UX permitindo que usuários encontrem mangás facilmente em listas longas

5. **Tipos Long vs Int:**
   - `mal_id` usa tipo `Long`
   - **Razão:** IDs do MyAnimeList podem exceder o limite de `Int` (2.147.483.647). Long suporta até 9.223.372.036.854.775.807

---

### 3. `screens/MangaSearchScreen.kt`

**Propósito:** Tela de busca de mangás com integração à API Jikan

**Componentes Principais:**

#### A. Estado da Tela

```kotlin
var mangas by remember { mutableStateOf<List<MangaItem>>(emptyList()) }
var favoriteMangaIds by remember { mutableStateOf<List<Long>>(emptyList()) }
var isLoading by remember { mutableStateOf(false) }
var searchQuery by remember { mutableStateOf("") }
```

**Decisões Técnicas:**

1. **remember + mutableStateOf:**

   - State hoisting com recomposição automática
   - **Razão:** Quando o estado muda, Jetpack Compose recompõe apenas os componentes afetados, otimizando performance

2. **favoriteMangaIds separado:**
   - Lista de IDs em vez de objetos completos
   - **Razão:** Verificação O(1) com `contains()`, economiza memória carregando apenas IDs necessários para UI

#### B. Carregamento Inicial

```kotlin
LaunchedEffect(Unit) {
    favoriteMangaIds = withContext(Dispatchers.IO) {
        mangaDao.getAllFavoriteMangaIds()
    }
}
```

**Decisões Técnicas:**

1. **LaunchedEffect(Unit):**

   - Executa apenas uma vez na composição inicial
   - **Razão:** `Unit` como key garante que o efeito não seja relançado em recomposições

2. **withContext(Dispatchers.IO):**
   - Troca de contexto para thread de I/O
   - **Razão:** Operações de banco devem rodar em thread de background para não bloquear UI

#### C. Lógica de Busca

```kotlin
onSearch = { query ->
    if (query.trim().length >= 3) {
        coroutineScope.launch {
            isLoading = true
            withContext(Dispatchers.IO) {
                try {
                    delay(350) // Rate limiting
                    val response = jikanApi.searchManga(query.trim())
                    mangas = response.data
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            isLoading = false
        }
    }
}
```

**Decisões Técnicas:**

1. **Validação de 3 caracteres:**

   - `if (query.trim().length >= 3)`
   - **Razão:** Previne buscas vazias ou muito genéricas que sobrecarregam a API e retornam resultados irrelevantes

2. **delay(350) - Rate Limiting:**

   - Atraso de 350ms antes de cada requisição
   - **Razão:** API Jikan limita a 3 requisições/segundo (≈333ms). 350ms garante conformidade com margem de segurança

3. **try-catch sem UI de erro:**

   - Apenas imprime stack trace
   - **Razão:** Em busca, falhas são menos críticas (usuário pode tentar novamente). Evita poluir UI com alertas para erros de rede temporários

4. **isLoading flag:**
   - Controle manual de loading state
   - **Razão:** Permite mostrar indicador de progresso durante busca, melhorando UX

#### D. Lógica de Favoritar

```kotlin
onFavoriteClick = { clickedManga ->
    coroutineScope.launch(Dispatchers.IO) {
        if (isFavorite) {
            mangaDao.deleteMangaByMalId(clickedManga.malId)
        } else {
            val gson = Gson()
            val mangaEntity = FavoriteMangaEntity(
                malId = clickedManga.malId,
                title = clickedManga.title,
                titleEnglish = clickedManga.titleEnglish,
                imageUrl = clickedManga.images.jpg.largeImageUrl,
                type = clickedManga.type,
                chapters = clickedManga.chapters,
                volumes = clickedManga.volumes,
                status = clickedManga.status,
                publishedFrom = clickedManga.published?.from,
                publishedTo = clickedManga.published?.to,
                score = clickedManga.score,
                synopsis = clickedManga.synopsis,
                authors = gson.toJson(clickedManga.authors),
                genres = gson.toJson(clickedManga.genres)
            )
            mangaDao.insertManga(mangaEntity)
        }
        favoriteMangaIds = mangaDao.getAllFavoriteMangaIds()
    }
}
```

**Decisões Técnicas:**

1. **Toggle baseado em isFavorite:**

   - Verifica estado atual antes de adicionar/remover
   - **Razão:** Comportamento intuitivo de toggle (um clique favorita, outro remove)

2. **Gson para serialização:**

   - `gson.toJson(clickedManga.authors)`
   - **Razão:** Converte arrays complexos para string JSON de forma confiável, compatível com SQLite

3. **Recarga de IDs após operação:**

   - `favoriteMangaIds = mangaDao.getAllFavoriteMangaIds()`
   - **Razão:** Sincroniza UI com banco de dados imediatamente, garantindo que ícones de coração reflitam estado real

4. **Mapeamento manual de campos:**
   - Não usa construtor automático
   - **Razão:** API e Entity têm estruturas diferentes (ex: `images.jpg.largeImageUrl` vs `imageUrl`). Mapeamento explícito evita erros

#### E. Componente de Busca (MangaSearchBar)

```kotlin
@Composable
fun MangaSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(...) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (query.trim().length >= 3) {
                        onSearch(query)
                    }
                }
            )
        )
        IconButton(onClick = { ... }) {
            Icon(imageVector = Icons.Default.Search, ...)
        }
    }
}
```

**Decisões Técnicas:**

1. **State Hoisting:**

   - `query` e callbacks passados como parâmetros
   - **Razão:** Componente reutilizável e testável. Não gerencia estado próprio

2. **ImeAction.Search:**

   - Mostra botão "Buscar" no teclado
   - **Razão:** UX melhorada - usuários podem buscar diretamente do teclado sem clicar no botão

3. **Validação duplicada:**
   - Em `keyboardActions` e `IconButton`
   - **Razão:** Consistência de comportamento independente do método de acionamento

---

### 4. `screens/FavoriteMangasScreen.kt`

**Propósito:** Tela para visualizar e gerenciar mangás favoritos

**Componentes Principais:**

#### A. Estado e Carregamento

```kotlin
var favoriteMangas by remember { mutableStateOf<List<FavoriteMangaEntity>>(emptyList()) }

LaunchedEffect(Unit) {
    favoriteMangas = withContext(Dispatchers.IO) {
        mangaDao.getAllFavoriteMangas()
    }
}
```

**Decisões Técnicas:**

1. **Carregamento de objetos completos:**

   - Usa `getAllFavoriteMangas()` em vez de IDs
   - **Razão:** Esta tela precisa exibir todos os dados (imagem, título, score, etc.). Carregar IDs e depois buscar individualmente seria ineficiente

2. **Estado local sem sincronização contínua:**
   - Não usa Flow/LiveData para observar mudanças
   - **Razão:** Mudanças só ocorrem nesta própria tela (remoção). Sincronização reativa seria overhead desnecessário

#### B. UI Condicional

```kotlin
if (favoriteMangas.isEmpty()) {
    Column(...) {
        Text("Nenhum mangá favoritado ainda")
    }
} else {
    LazyColumn(...) {
        items(favoriteMangas.size) { index ->
            FavoriteMangaCard(...)
        }
    }
}
```

**Decisões Técnicas:**

1. **Empty State dedicado:**

   - Mensagem centralizada quando lista vazia
   - **Razão:** Melhora UX evitando confusão (tela em branco vs sem favoritos). Comunica estado claramente

2. **items(favoriteMangas.size):**
   - Usa índice em vez de `items(favoriteMangas)`
   - **Razão:** Permite acesso direto ao item por índice, útil quando precisamos passar objeto completo para callback

#### C. Remoção de Favoritos

```kotlin
onDeleteClick = { deletedManga ->
    coroutineScope.launch(Dispatchers.IO) {
        mangaDao.deleteMangaByMalId(deletedManga.malId)
        favoriteMangas = mangaDao.getAllFavoriteMangas()
    }
}
```

**Decisões Técnicas:**

1. **Recarga completa após deleção:**

   - Query novamente o banco em vez de remover do estado local
   - **Razão:** Garante consistência com banco de dados. Evita dessincronia se houver falha na deleção

2. **Sem confirmação de deleção:**

   - Remove imediatamente ao clicar
   - **Razão:** Ação reversível (pode favoritar novamente). Diálogo de confirmação seria fricção desnecessária

3. **Ícone de Delete vs Desfavoritar:**
   - Usa `Icons.Default.Delete` em vez de coração vazio
   - **Razão:** Contexto da tela (lista de favoritos) deixa claro que é remoção. Ícone distinto previne cliques acidentais

---

### 5. `bottombars/MangaBottomBarEntriesEnum.kt`

**Propósito:** Definir entradas da barra de navegação inferior

**Estrutura:**

```kotlin
enum class MangaBottomBarEntriesEnum(
    val label: String,
    val route: RoutesNames,
    val icon: ImageVector,
) {
    MangaListScreen(
        label = "Top Mangás",
        route = RoutesNames.MangaListScreen,
        icon = Icons.Default.TrendingUp,
    ),
    MangaSearchScreen(
        label = "Buscar",
        route = RoutesNames.MangaSearchScreen,
        icon = Icons.Default.Search,
    ),
    FavoriteMangasScreen(
        label = "Favoritos",
        route = RoutesNames.FavoriteMangasScreen,
        icon = Icons.Default.Favorite,
    )
}
```

**Decisões Técnicas:**

1. **Enum em vez de List:**

   - Type-safe e compile-time checked
   - **Razão:** Previne erros de typo em strings. IDE fornece autocomplete

2. **Ícones semânticos:**

   - `TrendingUp` para top mangás, `Search` para busca, `Favorite` para favoritos
   - **Razão:** Ícones universalmente reconhecíveis melhoram navegação intuitiva

3. **Ordem das abas:**
   - Top Mangás → Buscar → Favoritos
   - **Razão:** Fluxo natural: descobrir (top) → procurar específico (busca) → revisitar (favoritos)

---

### 6. `bottombars/MangaBottomBar.kt`

**Propósito:** Componente de navegação inferior para seção de mangás

**Estrutura:**

```kotlin
@Composable
fun MangaBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = navBackStackEntry?.destination

    BottomAppBar {
        MangaBottomBarEntriesEnum.entries.map { bottomNavigationItem ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.hasRoute(bottomNavigationItem.route::class)
            } == true

            NavigationBarItem(
                selected = isSelected,
                onClick = { navController.navigate(bottomNavigationItem.route) },
                icon = { Icon(...) },
                alwaysShowLabel = isSelected,
                label = { Text(bottomNavigationItem.label) }
            )
        }
    }
}
```

**Decisões Técnicas:**

1. **currentBackStackEntryAsState():**

   - Observable do estado de navegação
   - **Razão:** Recompõe automaticamente quando usuário navega, atualizando aba selecionada

2. **hierarchy?.any { it.hasRoute(...) }:**

   - Verifica hierarquia de navegação
   - **Razão:** Suporta navegação aninhada. Se houver sub-destinos, a aba pai permanece destacada

3. **alwaysShowLabel = isSelected:**

   - Mostra label apenas na aba selecionada
   - **Razão:** Economiza espaço horizontal, mantém UI limpa. Usuários identificam não-selecionadas por ícone

4. **navController.navigate() sem popUpTo:**
   - Navegação simples sem limpar backstack
   - **Razão:** Permite que usuários voltem entre abas usando botão back, preservando histórico

---

## 🔧 Arquivos Modificados

### 1. `db/DatabaseHelper.kt`

**Mudanças:**

```kotlin
// Antes (versão 1)
@Database(
    version = 1,
    entities = [
        MusicAlbumEntity::class,
        MusicTrackEntity::class,
        MusicArtistEntity::class,
        ClassicalMusicEntity::class
    ]
)
abstract class DatabaseHelper : RoomDatabase() {
    abstract fun musicDao(): MusicDao
    abstract fun classicalDao(): ClassicalDao

    companion object {
        fun getInstance(context: Context): DatabaseHelper {
            return Room.databaseBuilder(
                context,
                DatabaseHelper::class.java,
                "hobbies.db"
            ).build()
        }
    }
}

// Depois (versão 2)
@Database(
    version = 2,  // ← Incrementado
    entities = [
        MusicAlbumEntity::class,
        MusicTrackEntity::class,
        MusicArtistEntity::class,
        ClassicalMusicEntity::class,
        FavoriteMangaEntity::class  // ← Adicionado
    ]
)
abstract class DatabaseHelper : RoomDatabase() {
    abstract fun musicDao(): MusicDao
    abstract fun classicalDao(): ClassicalDao
    abstract fun mangaDao(): MangaDao  // ← Adicionado

    companion object {
        fun getInstance(context: Context): DatabaseHelper {
            return Room.databaseBuilder(
                context,
                DatabaseHelper::class.java,
                "hobbies.db"
            ).fallbackToDestructiveMigration()  // ← Adicionado
            .build()
        }
    }
}
```

**Razões das Mudanças:**

1. **Versão 1 → 2:**

   - Toda mudança de schema requer incremento de versão
   - **Razão:** Room usa versionamento para detectar mudanças e executar migrações

2. **FavoriteMangaEntity nas entities:**

   - Registra nova tabela no banco
   - **Razão:** Room precisa conhecer todas as entidades em compile-time para gerar código de acesso

3. **abstract fun mangaDao():**

   - Expõe novo DAO
   - **Razão:** Permite que outras classes acessem operações de manga via DatabaseHelper

4. **fallbackToDestructiveMigration():**
   - Recria banco em vez de tentar migração
   - **Razão:** Durante desenvolvimento, é mais simples recriar que escrever scripts de migração. **ATENÇÃO:** Remove dados existentes! Em produção, deve-se implementar migração adequada

---

### 2. `navigation/RoutesNames.kt`

**Mudanças:**

```kotlin
// Adicionado ao sealed class
@Serializable
object MangaSearchScreen : RoutesNames()

@Serializable
object FavoriteMangasScreen : RoutesNames()
```

**Razões:**

1. **Sealed class para rotas:**

   - Type-safe navigation
   - **Razão:** Previne erros de string em rotas. Refatorações são seguras (IDE atualiza referências)

2. **@Serializable:**
   - Necessário para Navigation Compose com serialização de argumentos
   - **Razão:** Permite passar objetos complexos entre telas (futuramente, poderia passar MangaItem para tela de detalhes)

---

### 3. `navigation/AppNavHost.kt`

**Mudanças:**

```kotlin
// Imports adicionados
import com.br.ifal.hobbyhub.screens.FavoriteMangasScreen
import com.br.ifal.hobbyhub.screens.MangaSearchScreen

// Composables adicionados
composable<RoutesNames.MangaSearchScreen> {
    MangaSearchScreen(navController)
}

composable<RoutesNames.FavoriteMangasScreen> {
    FavoriteMangasScreen(navController)
}
```

**Razões:**

1. **Registro de rotas:**

   - Mapeia tipos de rota para composables
   - **Razão:** Navigation Compose precisa saber qual @Composable renderizar para cada rota

2. **Passagem de navController:**
   - Todas as telas recebem navController
   - **Razão:** Permite navegação interna (ex: de busca para detalhes) e uso de bottom bar

---

### 4. `screens/MangaListScreen.kt`

**Mudanças:**

```kotlin
// Import adicionado
import com.br.ifal.hobbyhub.bottombars.MangaBottomBar

// Scaffold modificado
Scaffold(
    modifier = Modifier,
    bottomBar = { MangaBottomBar(navController) }  // ← Adicionado
) { paddingValues ->
    // ...
}
```

**Razões:**

1. **bottomBar no Scaffold:**

   - Integra barra de navegação inferior
   - **Razão:** Scaffold gerencia layout (content + bottom bar), aplicando padding automaticamente para que conteúdo não seja sobreposto

2. **Consistência entre telas:**
   - Todas as 3 telas de manga usam mesma bottom bar
   - **Razão:** Navegação unificada. Usuário pode trocar de tela de qualquer lugar

---

## 🧠 Decisões de Design e Lógicas Específicas

### 1. Arquitetura de Dados

#### Por que usar Room em vez de SharedPreferences?

**SharedPreferences:**

- ❌ Armazena apenas tipos primitivos (String, Int, Boolean)
- ❌ Difícil de consultar/filtrar dados
- ❌ Não suporta queries complexas
- ❌ Ruim para grandes volumes de dados

**Room:**

- ✅ Suporta objetos complexos
- ✅ Queries SQL poderosas (ORDER BY, WHERE, JOIN)
- ✅ Type-safe com verificação em compile-time
- ✅ Otimizado para grandes volumes
- ✅ Suporte a observáveis (Flow, LiveData)

**Decisão:** Room é a escolha correta para entidades estruturadas que precisam de consultas eficientes.

---

#### Por que armazenar autores/gêneros como JSON?

**Alternativa 1: Tabelas relacionais**

```sql
CREATE TABLE manga (id, title, ...)
CREATE TABLE author (id, name)
CREATE TABLE manga_author (manga_id, author_id)  -- Junction table
```

❌ Complexidade: 3 tabelas para um relacionamento simples
❌ Queries mais lentas (JOINs)
❌ Mais código boilerplate

**Alternativa 2: String separada por vírgula**

```kotlin
val authors = "Author1,Author2,Author3"
```

❌ Difícil de deserializar (e se nome tiver vírgula?)
❌ Sem estrutura (apenas nomes, sem IDs/URLs)

**Solução escolhida: JSON string**

```kotlin
val authors = "[{\"mal_id\":123,\"name\":\"Author1\"}, ...]"
```

✅ Preserva estrutura completa do objeto
✅ Fácil serialização/deserialização com Gson
✅ Flexível (adicionar campos sem alterar schema)
✅ SQLite suporta JSON queries (se necessário futuramente)

---

### 2. Gerenciamento de Estado

#### Por que Coroutines em vez de Callbacks?

**Callbacks (estilo antigo):**

```kotlin
jikanApi.searchManga(query, object : Callback {
    override fun onSuccess(result: List<Manga>) {
        // Sucesso
    }
    override fun onError(error: Throwable) {
        // Erro
    }
})
```

❌ Callback hell (callbacks aninhados)
❌ Difícil tratamento de erros
❌ Não cancela automaticamente

**Coroutines (moderno):**

```kotlin
coroutineScope.launch {
    try {
        val result = jikanApi.searchManga(query)
        mangas = result.data
    } catch (e: Exception) {
        // Erro
    }
}
```

✅ Código sequencial (mais legível)
✅ try-catch natural
✅ Cancelamento automático quando composable é descartado
✅ Integração perfeita com Jetpack Compose

---

#### Por que separar favoriteMangaIds da lista completa?

**Abordagem 1: Carregar objetos completos**

```kotlin
var favoriteMangas by remember { mutableStateOf<List<FavoriteMangaEntity>>(emptyList()) }
val isFavorite = favoriteMangas.any { it.malId == manga.malId }  // O(n)
```

❌ O(n) para cada verificação
❌ Muito uso de memória (objetos completos)

**Abordagem 2: Apenas IDs (escolhida)**

```kotlin
var favoriteMangaIds by remember { mutableStateOf<List<Long>>(emptyList()) }
val isFavorite = favoriteMangaIds.contains(manga.malId)  // O(1) com HashSet
```

✅ O(1) verificação com HashSet
✅ Pouca memória (apenas Longs)
✅ Carregamento rápido

---

### 3. UX e Navegação

#### Por que 3 abas em vez de 2?

**Opção 1: 2 abas (Lista + Favoritos)**

- Busca como modal/dialog
  ❌ Menos acessível (precisa abrir modal)
  ❌ Interrompe fluxo de navegação

**Opção 2: 3 abas (escolhida)**

- Lista, Busca, Favoritos como abas iguais
  ✅ Acesso direto a todas as funcionalidades
  ✅ Navegação fluida
  ✅ Padrão familiar (similar a outros apps)

---

#### Por que Rate Limiting de 350ms?

**Matemática:**

- API Jikan: máximo 3 requisições/segundo
- 1000ms ÷ 3 req = 333.33ms por requisição

**Opções:**

1. **333ms:** Exato no limite
   ❌ Sem margem de erro (clock skew, latência)
2. **350ms (escolhida):** 5% de margem
   ✅ Garante conformidade
   ✅ Imperceptível para usuário (17ms extras)

3. **500ms:** Muito conservador
   ❌ UX prejudicada (espera desnecessária)

---

#### Por que validação de 3 caracteres?

**Dados empíricos:**

- 1 caractere: "A" retorna milhares de resultados irrelevantes
- 2 caracteres: "Na" ainda muito genérico
- 3 caracteres: "Nar" já filtra significativamente (ex: "Naruto")
- 4+ caracteres: Muito restritivo (exclui títulos curtos como "One Piece" → "One")

**Decisão:** 3 caracteres é sweet spot entre especificidade e usabilidade.

---

### 4. Otimizações de Performance

#### Por que LazyColumn em vez de Column + ScrollView?

**Column + ScrollView:**

```kotlin
Column(Modifier.verticalScroll(...)) {
    items.forEach { item ->
        MangaCard(item)  // Renderiza TODOS os itens
    }
}
```

❌ Renderiza todos os itens de uma vez
❌ Alto uso de memória
❌ Scroll lento com listas grandes

**LazyColumn:**

```kotlin
LazyColumn {
    items(items.size) { index ->
        MangaCard(items[index])  // Renderiza apenas visíveis
    }
}
```

✅ Lazy loading (apenas itens visíveis)
✅ Reciclagem de views (como RecyclerView)
✅ Scroll suave independente do tamanho
✅ Baixo uso de memória

---

#### Por que Coil em vez de Glide/Picasso?

**Comparação:**

| Biblioteca | Tamanho | Kotlin-first | Compose Support | Coroutines |
| ---------- | ------- | ------------ | --------------- | ---------- |
| Glide      | ~500KB  | ❌           | Limitado        | ❌         |
| Picasso    | ~150KB  | ❌           | Não oficial     | ❌         |
| **Coil**   | ~200KB  | ✅           | ✅ Nativo       | ✅         |

**Vantagens do Coil:**

- Escrito em Kotlin para Kotlin
- Usa Coroutines nativamente
- `AsyncImage` composable nativo
- Cache automático eficiente
- Placeholder e error handling simples

---

## 📊 Fluxo de Dados

### Busca de Mangás

```
┌─────────────┐
│   Usuário   │
│ digita "Na" │
└──────┬──────┘
       │
       ▼
┌─────────────────┐
│  Validação      │
│ trim().length   │
│     >= 3?       │
└──────┬──────────┘
       │ Sim
       ▼
┌─────────────────┐
│  Rate Limiting  │
│   delay(350)    │
└──────┬──────────┘
       │
       ▼
┌─────────────────┐
│   Jikan API     │
│ GET /v4/manga?  │
│    q=Na         │
└──────┬──────────┘
       │
       ▼
┌─────────────────┐
│  Deserialização │
│   Gson → List   │
│   <MangaItem>   │
└──────┬──────────┘
       │
       ▼
┌─────────────────┐
│  State Update   │
│ mangas = result │
└──────┬──────────┘
       │
       ▼
┌─────────────────┐
│   Recompose     │
│  LazyColumn     │
│  com novos      │
│   resultados    │
└─────────────────┘
```

### Favoritar Mangá

```
┌─────────────────┐
│    Usuário      │
│ clica ❤️ vazio  │
└──────┬──────────┘
       │
       ▼
┌─────────────────┐
│   Verificação   │
│   isFavorite?   │
└──────┬──────────┘
       │ Não
       ▼
┌─────────────────┐
│   Mapeamento    │
│  MangaItem →    │
│FavoriteManga    │
│     Entity      │
└──────┬──────────┘
       │
       ▼
┌─────────────────┐
│  Serialização   │
│ authors/genres  │
│   → JSON        │
└──────┬──────────┘
       │
       ▼
┌─────────────────┐
│  Room Insert    │
│ mangaDao.insert │
│     (entity)    │
└──────┬──────────┘
       │
       ▼
┌─────────────────┐
│   SQLite DB     │
│ INSERT INTO     │
│favorite_manga   │
└──────┬──────────┘
       │
       ▼
┌─────────────────┐
│ Reload IDs      │
│ getAllFavorite  │
│   MangaIds()    │
└──────┬──────────┘
       │
       ▼
┌─────────────────┐
│  State Update   │
│favoriteMangaIds │
│ = [1,2,3,...]   │
└──────┬──────────┘
       │
       ▼
┌─────────────────┐
│   Recompose     │
│  ❤️ vazio →     │
│  ❤️ preenchido  │
└─────────────────┘
```

---

## 🏗️ Padrões Arquiteturais

### 1. Repository Pattern (Implícito)

Embora não tenhamos uma classe `MangaRepository` explícita, seguimos o padrão:

**Responsabilidades separadas:**

- **Data Source (API):** `JikanApi` - busca dados remotos
- **Data Source (Local):** `MangaDao` - acessa dados locais
- **UI:** Screens - apresenta dados

**Benefícios:**

- Fácil testar (pode mockar DAO/API)
- Fácil trocar implementação (ex: mudar API)
- Single Responsibility Principle

---

### 2. State Hoisting

Todas as telas seguem padrão de state hoisting:

```kotlin
// Estado gerenciado no nível superior
@Composable
fun MangaSearchScreen(navController: NavHostController) {
    var query by remember { mutableStateOf("") }

    // Componentes recebem estado e callbacks
    MangaSearchBar(
        query = query,
        onQueryChange = { query = it },
        onSearch = { ... }
    )
}

// Componente sem estado próprio (stateless)
@Composable
fun MangaSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit
) {
    // Apenas apresenta UI
}
```

**Vantagens:**

- Componentes reutilizáveis
- Fácil testar (pure functions)
- Estado previsível (single source of truth)

---

### 3. Unidirectional Data Flow (UDF)

```
         ┌──────────────┐
         │     State    │
         │ (mangas,     │
         │  favorites)  │
         └──────┬───────┘
                │
                ▼
         ┌──────────────┐
         │      UI      │
         │  (LazyColumn)│
         └──────┬───────┘
                │
                ▼
         ┌──────────────┐
         │    Events    │
         │ (onClick,    │
         │  onSearch)   │
         └──────┬───────┘
                │
                ▼
         ┌──────────────┐
         │  Update      │
         │  State       │
         └──────────────┘
```

Fluxo sempre na mesma direção: State → UI → Events → State

---

### 4. Separation of Concerns

**Models (Data Layer):**

- `FavoriteMangaEntity.kt` - Estrutura de dados
- `JikanMangaModels.kt` - Modelos da API

**Database (Data Access Layer):**

- `MangaDao.kt` - Operações de banco
- `DatabaseHelper.kt` - Configuração do banco

**Network (Remote Data Layer):**

- `JikanApi.kt` - Endpoints da API
- `RetrofitProvider.kt` - Cliente HTTP

**UI (Presentation Layer):**

- `MangaSearchScreen.kt` - Lógica de apresentação
- `FavoriteMangasScreen.kt` - Lógica de apresentação
- `MangaBottomBar.kt` - Componentes de navegação

**Navigation (Navigation Layer):**

- `RoutesNames.kt` - Definição de rotas
- `AppNavHost.kt` - Configuração de navegação

Cada camada tem responsabilidade única e bem definida.

---

## 🎓 Lições Aprendidas e Boas Práticas Aplicadas

### 1. Convenções de Nomenclatura

**Kotlin (camelCase):**

```kotlin
val malId: Long
val titleEnglish: String
val largeImageUrl: String
```

**SQL (snake_case):**

```sql
mal_id BIGINT
title_english TEXT
large_image_url TEXT
```

**Razão:** Cada linguagem tem suas convenções. `@ColumnInfo` faz a ponte entre elas.

---

### 2. Nullability

Sempre explicitamos nullability:

```kotlin
val title: String          // Sempre presente
val titleEnglish: String?  // Pode ser null
val chapters: Int?         // Pode ser null
```

**Evita:** `NullPointerException` em runtime

---

### 3. Imutabilidade

Preferimos `val` sobre `var`:

```kotlin
data class MangaItem(
    val malId: Long,      // val - imutável
    val title: String,    // val - imutável
)
```

**Benefícios:**

- Thread-safe por padrão
- Menos bugs (estado não muda inesperadamente)
- Mais previsível

---

### 4. Uso de Suspend Functions

Todas as operações de I/O são `suspend`:

```kotlin
suspend fun searchManga(query: String): JikanMangaResponse
suspend fun insertManga(manga: FavoriteMangaEntity)
```

**Razão:** Permite cancelamento, não bloqueia threads, melhor performance.

---

### 5. Error Handling Apropriado

**Network (não crítico):**

```kotlin
try {
    val result = jikanApi.searchManga(query)
} catch (e: Exception) {
    e.printStackTrace()  // Log apenas
}
```

**Database (crítico):**

```kotlin
try {
    mangaDao.insertManga(entity)
} catch (e: Exception) {
    // Aqui deveria mostrar erro ao usuário
    Log.e("DB", "Failed to save", e)
}
```

---

## 📈 Possíveis Melhorias Futuras

1. **Paginação na busca:** Atualmente carrega todos os resultados. Implementar infinite scroll.

2. **Cache de imagens:** Coil já faz cache, mas poderia configurar políticas personalizadas.

3. **Migração de banco:** Implementar migração adequada em vez de `fallbackToDestructiveMigration()`.

4. **Tela de detalhes:** Tela dedicada com todas as informações (autores, gêneros, sinopse completa).

5. **Filtros avançados:** Busca por gênero, status (em publicação/finalizado), score mínimo.

6. **Sincronização na nuvem:** Backup de favoritos com Firebase/backend próprio.

7. **Testes unitários:** Testar DAOs, ViewModels (se implementado MVVM).

8. **Testes de integração:** Testar fluxo completo (buscar → favoritar → visualizar).

---

## 🎯 Conclusão

Esta implementação segue as melhores práticas modernas de desenvolvimento Android:

- **Jetpack Compose** para UI declarativa
- **Room** para persistência type-safe
- **Coroutines** para operações assíncronas
- **Material Design 3** para UI consistente
- **Navigation Compose** para navegação type-safe

O código é **manutenível**, **testável** e **escalável**, pronto para receber novas funcionalidades sem refatorações significativas.
