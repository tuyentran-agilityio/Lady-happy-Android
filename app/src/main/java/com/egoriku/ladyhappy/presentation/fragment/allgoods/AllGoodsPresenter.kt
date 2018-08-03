package com.egoriku.ladyhappy.presentation.fragment.allgoods

import com.egoriku.core.common.TrackingConstants
import com.egoriku.core.di.utils.IAnalyticsHelper
import com.egoriku.corelib_kt.arch.BasePresenter
import com.egoriku.ladyhappy.domain.interactors.allgoods.CategoriesUseCase
import com.egoriku.ladyhappy.domain.interactors.allgoods.NewsUseCase
import com.egoriku.core.usecase.Params
import com.egoriku.ladyhappy.domain.models.CategoriesModel
import com.egoriku.ladyhappy.domain.models.NewsModel
import com.egoriku.core.usecase.DefaultObserver
import javax.inject.Inject

class AllGoodsPresenter
@Inject constructor(
        private val categoriesUseCase: CategoriesUseCase,
        private val newsUseCase: NewsUseCase,
        private val analyticsHelper: IAnalyticsHelper
) : BasePresenter<AllGoodsContract.View>(), AllGoodsContract.Presenter {

    private var screenModel = AllGoodsScreenModel()

    override fun onPresenterCreated() {
        super.onPresenterCreated()
        analyticsHelper.trackPageView(TrackingConstants.FRAGMENT_ALL_GOODS)
    }

    override fun onPresenterDestroy() {
        categoriesUseCase.dispose()
        newsUseCase.dispose()
        super.onPresenterDestroy()
    }

    override fun loadData() {
        if (isViewAttached) {
            view.showLoading()

            if (screenModel.categoriesEmpty() && screenModel.newsEmpty()) {
                categoriesUseCase.execute(CategoriesObserver(), Params.EMPTY)
                newsUseCase.execute(NewsObserver(), Params.EMPTY)
            } else {
                view.hideLoading()
                view.render(screenModel)
            }
        }
    }

    override fun onGetCategoriesSuccess(categoriesModel: CategoriesModel) {
        if (isViewAttached) {
            screenModel.categories = categoriesModel.categories

            view.hideLoading()
            view.render(screenModel)
        }
    }

    override fun onGetNewsSuccess(newsModel: NewsModel) {
        if (isViewAttached) {
            screenModel.news = newsModel.news

            view.render(screenModel)
        }
    }

    override fun onGetCategoriesError(e: Throwable) {
        if (isViewAttached) {
            view.hideLoading()
        }
    }

    override fun onGetNewsError(e: Throwable) {
        if (isViewAttached) {
            view.hideLoading()
        }
    }

    override fun onCategoryClickSuccessTracking() {

    }

    override fun onGetCategoriesSuccessTracking() {
        analyticsHelper.trackGetCategoriesSuccess(null)
    }

    override fun onGetCategoriesErrorTracking() {
        analyticsHelper.trackGetCategoriesFail(null)
    }

    override fun onGetNewsSuccessTracking() {

    }

    override fun onGetNewsErrorTracking() {

    }

    private inner class CategoriesObserver : DefaultObserver<CategoriesModel>() {

        override fun onError(exception: Throwable) {
            onGetCategoriesErrorTracking()
            onGetCategoriesError(exception)
        }

        override fun onNext(t: CategoriesModel) {
            onGetCategoriesSuccessTracking()
            onGetCategoriesSuccess(t)
        }
    }

    private inner class NewsObserver : DefaultObserver<NewsModel>() {
        override fun onNext(t: NewsModel) {
            onGetNewsSuccessTracking()
            onGetNewsSuccess(t)
        }

        override fun onError(exception: Throwable) {
            onGetNewsErrorTracking()
            onGetNewsError(exception)
        }
    }
}