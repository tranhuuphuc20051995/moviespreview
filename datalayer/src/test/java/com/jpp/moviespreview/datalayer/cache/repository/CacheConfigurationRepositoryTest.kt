package com.jpp.moviespreview.datalayer.cache.repository

import com.jpp.moviespreview.datalayer.AppConfiguration
import com.jpp.moviespreview.datalayer.cache.MPDataBase
import com.jpp.moviespreview.datalayer.cache.timestamp.MPTimestamps
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CacheConfigurationRepositoryTest {


    @RelaxedMockK
    private lateinit var mpCache: MPTimestamps
    @RelaxedMockK
    private lateinit var mpDatabase: MPDataBase

    private lateinit var subject: CacheConfigurationRepository

    @BeforeEach
    fun setUp() {
        subject = CacheConfigurationRepository(mpCache, mpDatabase)
    }

    @Test
    @DisplayName("Should retrieve data from DB when cache is valid ")
    fun getConfiguration_whenDataIsCached() {
        val expected = mockk<AppConfiguration>()

        every { mpCache.isAppConfigurationUpToDate() } returns true
        every { mpDatabase.getStoredAppConfiguration() } returns expected

        val actual = subject.getConfiguration()

        assertEquals(expected, actual)

        verify { mpDatabase.getStoredAppConfiguration() }
    }

    @Test
    @DisplayName("Should return null when data in cache is out of date ")
    fun getConfiguration_whenDataInCacheIsOutdated() {
        val expected = mockk<AppConfiguration>()

        every { mpCache.isAppConfigurationUpToDate() } returns false
        every { mpDatabase.getStoredAppConfiguration() } returns expected

        val actual = subject.getConfiguration()

        assertNull(actual)

        verify(exactly = 0) { mpDatabase.getStoredAppConfiguration() }
    }

    @Test
    @DisplayName("Should update database and cache when updating app configuration")
    fun updateAppConfiguration() {
        val appConfigMock = mockk<AppConfiguration>()

        subject.updateAppConfiguration(appConfigMock)

        verify { mpDatabase.updateAppConfiguration(appConfigMock) }
        verify { mpCache.updateAppConfigurationInserted() }
    }

}