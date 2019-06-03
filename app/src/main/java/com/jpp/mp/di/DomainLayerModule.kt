package com.jpp.mp.di

import com.jpp.mpdomain.interactors.ImagesPathInteractor
import com.jpp.mpdomain.repository.*
import com.jpp.mpdomain.usecase.about.GetAboutNavigationUrlUseCase
import com.jpp.mpdomain.usecase.account.GetMovieAccountStateUseCase
import com.jpp.mpdomain.usecase.account.MarkMovieAsFavoriteUseCase
import com.jpp.mpdomain.usecase.appversion.GetAppVersionUseCase
import com.jpp.mpdomain.usecase.credits.ConfigCastCharacterUseCase
import com.jpp.mpdomain.usecase.credits.GetCreditsUseCase
import com.jpp.mpdomain.usecase.details.GetMovieDetailsUseCase
import com.jpp.mpdomain.usecase.licenses.GetAppLicensesUseCase
import com.jpp.mpdomain.usecase.licenses.GetLicenseUseCase
import com.jpp.mpdomain.usecase.movies.ConfigMovieUseCase
import com.jpp.mpdomain.usecase.movies.GetMoviesUseCase
import com.jpp.mpdomain.usecase.person.GetPersonUseCase
import com.jpp.mpdomain.usecase.search.ConfigSearchResultUseCase
import com.jpp.mpdomain.usecase.search.SearchUseCase
import com.jpp.mpdomain.usecase.support.RefreshAppDataUseCase
import com.jpp.mpdomain.usecase.support.RefreshLanguageDataUseCase
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Singleton

/**
 * Provides all dependencies for the domain layer.
 */
@Module
class DomainLayerModule {

    @Provides
    @Singleton
    fun providesNetworkExecutor(): Executor = Executors.newFixedThreadPool(5)

    @Provides
    fun providesSearchUseCase(searchRepository: SearchRepository,
                              connectivityRepository: ConnectivityRepository,
                              languageRepository: LanguageRepository)
            : SearchUseCase = SearchUseCase.Impl(searchRepository, connectivityRepository, languageRepository)

    @Provides
    @Singleton
    fun providesConfigSearchResultUseCase(configurationRepository: ConfigurationRepository)
            : ConfigSearchResultUseCase = ConfigSearchResultUseCase.Impl(configurationRepository)

    @Provides
    fun providesGetMoviesUseCase(moviesRepository: MoviesRepository,
                                 connectivityRepository: ConnectivityRepository,
                                 languageRepository: LanguageRepository)
            : GetMoviesUseCase = GetMoviesUseCase.Impl(moviesRepository, connectivityRepository, languageRepository)

    @Provides
    fun providesConfigMovieUseCase(configurationRepository: ConfigurationRepository)
            : ConfigMovieUseCase = ConfigMovieUseCase.Impl(configurationRepository)

    @Provides
    fun providesGetMovieDetailsUseCase(moviesRepository: MoviesRepository,
                                       connectivityRepository: ConnectivityRepository,
                                       languageRepository: LanguageRepository)
            : GetMovieDetailsUseCase = GetMovieDetailsUseCase.Impl(moviesRepository, connectivityRepository, languageRepository)

    @Provides
    fun providesGetMovieAccountStateUseCase(sessionRepository: SessionRepository,
                                            accountRepository: AccountRepository,
                                            connectivityRepository: ConnectivityRepository)
            : GetMovieAccountStateUseCase = GetMovieAccountStateUseCase.Impl(sessionRepository, accountRepository, connectivityRepository)

    @Provides
    fun providesMarkMovieAsFavoriteUseCase(sessionRepository: SessionRepository,
                                           accountRepository: AccountRepository,
                                           connectivityRepository: ConnectivityRepository)
            : MarkMovieAsFavoriteUseCase = MarkMovieAsFavoriteUseCase.Impl(sessionRepository, accountRepository, connectivityRepository)

    @Provides
    fun providesGetPersonUseCase(personRepository: PersonRepository,
                                 connectivityRepository: ConnectivityRepository,
                                 languageRepository: LanguageRepository)
            : GetPersonUseCase = GetPersonUseCase.Impl(personRepository, connectivityRepository, languageRepository)

    @Provides
    fun providesGetCreditsUseCase(creditsRepository: CreditsRepository,
                                  connectivityRepository: ConnectivityRepository)
            : GetCreditsUseCase = GetCreditsUseCase.Impl(creditsRepository, connectivityRepository)

    @Provides
    fun providesConfigCastCharacterUseCase(configurationRepository: ConfigurationRepository)
            : ConfigCastCharacterUseCase = ConfigCastCharacterUseCase.Impl(configurationRepository)

    @Provides
    fun providesGetAppVersionUseCase(appVersionRepository: AppVersionRepository)
            : GetAppVersionUseCase = GetAppVersionUseCase.Impl(appVersionRepository)

    @Provides
    fun providesGetAboutNavigationUrlUseCase(aboutNavigationRepository: AboutNavigationRepository)
            : GetAboutNavigationUrlUseCase = GetAboutNavigationUrlUseCase.Impl(aboutNavigationRepository)

    @Provides
    fun providesGetAppLicensesUseCase(licensesRepository: LicensesRepository)
            : GetAppLicensesUseCase = GetAppLicensesUseCase.Impl(licensesRepository)

    @Provides
    fun providesGetLicenseUseCase(licensesRepository: LicensesRepository)
            : GetLicenseUseCase = GetLicenseUseCase.Impl(licensesRepository)

    @Provides
    fun providesRefreshDataUseCase(languageRepository: LanguageRepository,
                                   supportRepository: SupportRepository)
            : RefreshLanguageDataUseCase = RefreshLanguageDataUseCase.Impl(languageRepository, supportRepository)

    @Provides
    fun providesRefreshAppDataUseCase(accountRepository: AccountRepository, languageRepository: LanguageRepository)
            : RefreshAppDataUseCase = RefreshAppDataUseCase.Impl(accountRepository, languageRepository)


    @Provides
    fun providesImagesPathInteractor(configurationRepository: ConfigurationRepository)
            : ImagesPathInteractor = ImagesPathInteractor.Impl(configurationRepository)

}