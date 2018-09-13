package com.egoriku.landingfragment.presentation

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.egoriku.core.IApplication
import com.egoriku.core.model.ILandingModel
import com.egoriku.ui.ktx.gone
import com.egoriku.ui.ktx.show
import com.egoriku.landingfragment.R
import com.egoriku.landingfragment.di.LandingFragmentComponent
import com.egoriku.landingfragment.presentation.controller.*
import com.egoriku.ui.arch.fragment.BaseInjectableFragment
import com.egoriku.landingfragment.common.parallax.ParallaxScrollListener
import com.egoriku.ui.ktx.browseUrl
import kotlinx.android.synthetic.main.fragment_main_page.*
import ru.surfstudio.android.easyadapter.EasyAdapter
import ru.surfstudio.android.easyadapter.ItemList
import javax.inject.Inject

internal class LandingPageFragment : BaseInjectableFragment<LandingPageContract.View, LandingPageContract.Presenter>(), LandingPageContract.View {

    @Inject
    lateinit var landingPagePresenter: LandingPageContract.Presenter

    private val mainPageAdapter = EasyAdapter()
    private lateinit var parallaxScrollListener: ParallaxScrollListener

    private lateinit var headerController: HeaderController
    private lateinit var aboutController: AboutController
    private lateinit var quotasController: QuotesController
    private lateinit var sectionsHeaderController: SectionsHeaderController
    private lateinit var ourTeamController: OurTeamController

    companion object {
        fun newInstance() = LandingPageFragment()
    }

    override fun provideLayout(): Int = R.layout.fragment_main_page

    override fun initPresenter(): LandingPageContract.Presenter = landingPagePresenter

    override fun injectDependencies() {
        LandingFragmentComponent.Initializer
                .init((activity?.applicationContext as IApplication).getAppComponent())
                .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setTitle(R.string.navigation_main)
        initViews()
    }

    override fun initViews() {

        mainPageRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mainPageAdapter
        }

        parallaxScrollListener = ParallaxScrollListener().apply {
            initWith(mainPageRecyclerView, lifecycle)
        }

        headerController = HeaderController()
        aboutController = AboutController()
        sectionsHeaderController = SectionsHeaderController()
        quotasController = QuotesController(parallaxScrollListener)
        ourTeamController = OurTeamController {
            browseUrl(it)
        }

        presenter.loadLandingData()
    }

    override fun showLoading() {
        mainProgressBar.show()
    }

    override fun hideLoading() {
        mainProgressBar.gone()
    }

    override fun render(model: ILandingModel) {
        mainPageAdapter.setItems(
                ItemList.create()
                        .add(headerController)
                        .add(model.aboutInfo, aboutController)
                        .addIf(true, R.string.adapter_item_header_quotes, sectionsHeaderController)
                        .add(model.quote, quotasController)
                        .addIf(model.teamMembers.isNotEmpty(), R.string.adapter_item_header_our_team, sectionsHeaderController)
                        .addAll(model.teamMembers, ourTeamController)
        )
    }
}