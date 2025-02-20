package com.egoriku.ladyhappy.landing.presentation

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.egoriku.ladyhappy.core.feature.AboutUsFeature
import com.egoriku.ladyhappy.extensions.browseUrl
import com.egoriku.ladyhappy.extensions.gone
import com.egoriku.ladyhappy.extensions.visible
import com.egoriku.ladyhappy.landing.R
import com.egoriku.ladyhappy.landing.common.parallax.ParallaxScrollListener
import com.egoriku.ladyhappy.landing.databinding.FragmentLandingBinding
import com.egoriku.ladyhappy.landing.presentation.controller.*
import com.egoriku.ladyhappy.ui.controller.NoDataController
import org.koin.androidx.scope.ScopeFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.surfstudio.android.easyadapter.EasyAdapter
import ru.surfstudio.android.easyadapter.ItemList
import kotlin.properties.Delegates

class LandingPageFragment : ScopeFragment(R.layout.fragment_landing), AboutUsFeature {

    private val binding by viewBinding(FragmentLandingBinding::bind)

    private val landingViewModel by viewModel<LandingViewModel>()

    private var parallaxScrollListener: ParallaxScrollListener? = null

    private val landingAdapter = EasyAdapter()

    private var headerController: HeaderController by Delegates.notNull()
    private var noDataController: NoDataController by Delegates.notNull()
    private var aboutController: AboutController by Delegates.notNull()
    private var quotesController: QuotesController by Delegates.notNull()
    private var ourTeamController: OurTeamController by Delegates.notNull()
    private var sectionsHeaderController: SectionsHeaderController by Delegates.notNull()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        landingViewModel.screenState.observe(viewLifecycleOwner) {
            render(it)
        }

        initViews()
    }

    private fun initViews() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = landingAdapter
        }

        parallaxScrollListener = ParallaxScrollListener().apply {
            initWith(binding.recyclerView, lifecycle)
        }

        headerController = HeaderController()
        noDataController = NoDataController {
            landingViewModel.retryLoading()
        }

        aboutController = AboutController()
        sectionsHeaderController = SectionsHeaderController()
        quotesController = QuotesController(parallaxScrollListener)
        ourTeamController = OurTeamController(parallaxScrollListener) {
            browseUrl(it)
        }
    }

    private fun render(screenModel: LandingScreenModel) {
        val itemList = ItemList.create()

        when (screenModel.loadState) {
            LoadState.PROGRESS -> showProgress()
            else -> hideProgress()
        }

        itemList.addIf(
            screenModel.isEmpty() && screenModel.loadState == LoadState.ERROR_LOADING,
            noDataController
        )

        screenModel.landingModel?.let {
            itemList.add(headerController)
                .add(it.aboutInfo, aboutController)
                .addIf(
                    it.quotes.isNotEmpty(),
                    R.string.landing_adapter_item_header_quotes,
                    sectionsHeaderController
                )
                .addIf(it.quotes.isNotEmpty(), it.quotes, quotesController)
                .addIf(
                    it.teamMembers.isNotEmpty(),
                    R.string.landing_adapter_item_header_our_team,
                    sectionsHeaderController
                )
                .addAll(it.teamMembers, ourTeamController)
        }

        landingAdapter.setItems(itemList)
    }

    private fun showProgress() = with(binding.hatsProgressAnimationView) {
        startAnimation()
        visible()
    }

    private fun hideProgress() = with(binding.hatsProgressAnimationView) {
        stopAnimation()
        gone()
    }

    override fun onDestroy() {
        super.onDestroy()
        parallaxScrollListener = null
    }
}