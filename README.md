## Парыгина Мария Алексеевна, Б9124-09.03.03пикд 4

Приложение на основе данных open-API TVmazeAPI, использует список телевизионных шоу.
В ходе данной работы было реализовано приложение с использованием Hilt и Room. Room я использую для сценария добавления шоу в понравившиеся, которые добавляются во влкадку Favorites по нажатию на кнопку-иконку.
В Room хранится таблица "favorite_tvshow", которая сохраняет параметры id, name, network, genres, rating (то, что содержится на главной странице для каждого шоу).

## Выполненные тесты:
## Выполнено 8 юнит-тестов,
<h4> Юнит-тесты </h4>
<h3> ListViewModel: </h3>
 // 1 - начальное состояние
    @Test
    fun `initial state is loading`

// 2 - успешная загрузка
    @Test
    fun `loadShows success state with shows`

// 3 - ошибка загрузки
    @Test
    fun `loadShows error state`

// 4 - поиск с результатами
    @Test
    fun `search with results shows success`

// 5 - refresh сбрасывает состояние и перезагружает
    @Test
    fun `refresh clears state and reloads shows`

<h3> DetailsViewModel: </h3>
// 1 - успешная загрузка деталей
    @Test
    fun `loadShow success sets Success state`

// 2 - ошибка загрузки
    @Test
    fun `loadShow error sets Error state`

// 3 - загрузка несуществующего шоу
    @Test
    fun `loadShow returns Error when show is null`

<h4> Интеграционные тесты </h4>
<h3> RepositoryViewModel: </h3>
// 1 - repository + room: добавление и получение избранного
    @Test
    fun `add favorite and retrieve from Room`

// 2 - удаление из избранного
    @Test
    fun `remove favorite works correctly`
    
<h4> Тесты на проверку Flow </h4>
<h3> ListViewModel: </h3>
// 1 - flow: тестирование потока состояний loading - success
    @Test
    fun `stateFlow emits Loading then Success sequence`

// НЕТРИВИАЛЬНЫЙ FLOW
// 2 - flow: отмена устаревшего поиска
    @Test
    fun `rapid search cancels previous request and shows latest result`
    
<h4> Нетривиальные тесты </h4>
<h3> ListViewModel: </h3>
// 1 - retry после ошибки
    @Test
    fun `retry after error call to repository again and recovers`

// 2 - toggleFavorite обновляет состояние
    @Test
    fun `toggleFavorite updates isFavorite flag in state`

+ 3 - нетривиальный FLOW
    
<h3> RepositoryViewModel: </h3>
// нет дублей при повторном добавлении карточки
    @Test
    fun `no duplicate favorite creating`


## Сценарий использования:
<h4> Получает все избранные шоу, этот метод используется на странице Favorites </h4>
@Query("SELECT * FROM favorite_tvshow ORDER BY name")

suspend fun getFavorites(): List<FavoriteTvShowEntity>


<h4> Получает только id избранных шоу, нужно для помечания понравившихся шоу на главной странице при обновлении экрана и при нажатии на "Load Shows" </h4>
@Query("SELECT id FROM favorite_tvshow")

suspend fun getFavoritesIds(): List<Int>


<h4> Добавление шоу в список понравившихся </h4>
@Insert(onConflict = OnConflictStrategy.REPLACE)

suspend fun upsert(tvshow: FavoriteTvShowEntity)


<h4> Удаление шоу из списка (сущности) понравившихся шоу </h4>
@Query("DELETE FROM favorite_tvshow WHERE id = :id")

suspend fun deleteById(id: Int)


<h4> Функция для кнопки сердечка </h4>
fun toggleFavorite(show: TvShow, isFavorite: Boolean) {

    if (isFavorite) {
    
        dao.upsert(show.toFavoriteEntity())           - добавение в сущность избранных шоу
        
    } else {
    
        dao.deleteById(show.id)                       - удаления шоу из этого списка
        
    }
    
}


## Скриншоты
Экран загрузки приложения:

<img width="423" height="889" alt="image" src="https://github.com/user-attachments/assets/23c30f9a-8c52-43e2-a431-cbca294bf180" />

Экран ошибки загрузки приложения (при отключенном интернете):

<img width="425" height="886" alt="image" src="https://github.com/user-attachments/assets/930f9405-afd1-457c-8ab6-872c201644fd" />

Экран со списком телешоу (TvShowListScreen):

<img width="408" height="867" alt="image" src="https://github.com/user-attachments/assets/799b4402-4d45-4d0a-bc94-a83d178eb039" />

Шоу подгружаются в количестве 25 шт., кнопка внизу экрана:

<img width="413" height="871" alt="image" src="https://github.com/user-attachments/assets/d57048ad-d747-4224-9d71-ed572bc7758a" />

Экран с деталями телешоу при нажатии на карточку (TvShowDetailsScreen):

<img width="425" height="881" alt="image" src="https://github.com/user-attachments/assets/e86df0eb-172f-4804-a446-a79bed664104" />

Экран со всеми понравившимися телешоу:

<img width="404" height="865" alt="image" src="https://github.com/user-attachments/assets/964353cf-83e9-4cb9-bc76-ff6c833ae4de" />

Экран загрузки деталей:

<img width="425" height="889" alt="image" src="https://github.com/user-attachments/assets/e3a518b0-fbb0-4c2a-b822-30a10002e8dc" />

Экран ошибки загрузки деталей:

<img width="424" height="888" alt="image" src="https://github.com/user-attachments/assets/dd8b1320-ba8a-4b86-8ac1-c8d347281d1e" />
