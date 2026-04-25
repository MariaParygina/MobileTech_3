## Парыгина Мария Алексеевна, Б9124-09.03.03пикд 4

Приложение на основе данных open-API TVmazeAPI, использует список телевизионных шоу.
В ходе данной работы было реализовано приложение с использованием Hilt и Room. Room я использую для сценария добавления шоу в понравившиеся, которые добавляются во влкадку Favorites по нажатию на кнопку-иконку.
В Room хранится таблица "favorite_tvshow", которая сохраняет параметры id, name, network, genres, rating (то, что содержится на главной странице для каждого шоу).

## Сценарий использования:
<p> Получает все избранные шоу, этот метод используется на странице Favorites </p>
<p> @Query("SELECT * FROM favorite_tvshow ORDER BY name")
    suspend fun getFavorites(): List<FavoriteTvShowEntity> <\p>

<p> Получает только id избранных шоу, нужно для помечания понравившихся шоу на главной странице при обновлении экрана и при нажатии на "Load Shows" </p>
@Query("SELECT id FROM favorite_tvshow")
suspend fun getFavoritesIds(): List<Int>

<p> Добавление шоу в список понравившихся </p>
@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun upsert(tvshow: FavoriteTvShowEntity)

<p> Удаление шоу из списка (сущности) понравившихся шоу </p>
@Query("DELETE FROM favorite_tvshow WHERE id = :id")
suspend fun deleteById(id: Int)

<p> Функция для кнопки сердечка </p>
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

<img width="424" height="886" alt="image" src="https://github.com/user-attachments/assets/267aa827-0dfb-4a0a-8514-97c8e269c9a2" />

Шоу подгружаются в количестве 25 шт., кнопка внизу экрана:

<img width="423" height="886" alt="image" src="https://github.com/user-attachments/assets/e439440d-f7d0-4c7f-bdc9-b1d4d676cce6" />

Экран с деталями телешоу при нажатии на карточку (TvShowDetailsScreen):

<img width="425" height="881" alt="image" src="https://github.com/user-attachments/assets/e86df0eb-172f-4804-a446-a79bed664104" />

Экран загрузки деталей:

<img width="425" height="889" alt="image" src="https://github.com/user-attachments/assets/e3a518b0-fbb0-4c2a-b822-30a10002e8dc" />

Экран ошибки загрузки деталей:

<img width="424" height="888" alt="image" src="https://github.com/user-attachments/assets/dd8b1320-ba8a-4b86-8ac1-c8d347281d1e" />
