package developer.marvel.com.data.repository.comics

import developer.marvel.com.data.repository.comics.datasource.ComicsLocalDataSource
import developer.marvel.com.data.repository.comics.datasource.ComicsRemoteDataSource
import developer.marvel.com.domain.entity.ComicDataWrapper
import developer.marvel.com.domain.repository.ComicsRepository
import io.reactivex.Single
import javax.inject.Inject

class ComicsRepositoryImpl @Inject constructor(
    private val comicsRemoteDataSource: ComicsRemoteDataSource,
    private val comicsLocalDataSource: ComicsLocalDataSource
) : ComicsRepository {

    override fun getComics(): Single<ComicDataWrapper.ComicData> =
        Single.concat(
            comicsLocalDataSource.getComics(),
            comicsRemoteDataSource.getComics().flatMap { comicsData ->
                comicsLocalDataSource.saveComics(comicsData)
                Single.just(comicsData)
            }
        ).filter { it.comics.isNotEmpty() }.firstOrError()

}