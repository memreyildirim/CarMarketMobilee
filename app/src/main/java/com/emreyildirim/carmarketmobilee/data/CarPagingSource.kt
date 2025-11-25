package com.emreyildirim.carmarketmobilee.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.emreyildirim.carmarketmobilee.model.CarDto
import com.emreyildirim.carmarketmobilee.service.CarService

class CarPagingSource(
    private val service: CarService,
    private val sortBy: String = "carId"
) : PagingSource<Int, CarDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CarDto> {
        return try {
            val page = params.key ?: 0
            val response = service.getPagedCars(
                page = page,
                size = params.loadSize,
                sortBy = sortBy,
                asc = false
            )

            LoadResult.Page(
                data = response.content,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (response.content.isEmpty() || page >= response.totalPages - 1) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CarDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}


