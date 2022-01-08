package com.ishanvaghani.moviefy.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.ishanvaghani.moviefy.R
import com.ishanvaghani.moviefy.databinding.ActivityMainBinding
import com.ishanvaghani.moviefy.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import android.graphics.PorterDuff
import androidx.core.view.iterator
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var installStateUpdatedListener: InstallStateUpdatedListener

    private lateinit var reviewInfo: ReviewInfo
    private lateinit var reviewManager: ReviewManager

    private val IN_APP_UPDATE = 100;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        binding.bottomNav.setupWithNavController(navController)

        initInAppUpdate()
        initReview()
    }

    private fun initReview() {
        reviewManager = ReviewManagerFactory.create(this)
        val request = reviewManager.requestReviewFlow()

        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                reviewInfo = task.result
            } else {
                showToast(this, "Something went wrong")
            }
        }
    }

    private fun giveReview() {
        val flow = reviewManager.launchReviewFlow(this, reviewInfo)
        flow.addOnCompleteListener {
            showToast(this, "Rating is completed")
        }
    }

    private fun initInAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener {
                if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    it.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                ) {
                    appUpdateManager.startUpdateFlowForResult(
                        it,
                        AppUpdateType.FLEXIBLE,
                        this,
                        IN_APP_UPDATE
                    )
                }
            }
            .addOnFailureListener {
                showToast(this,"Update Failed")
            }

        installStateUpdatedListener = InstallStateUpdatedListener {
            when {
                it.installStatus() == InstallStatus.DOWNLOADING -> {

                }
                it.installStatus() == InstallStatus.DOWNLOADED -> {
                    showAppDownloaded()
                }
                it.installStatus() == InstallStatus.INSTALLED -> {
                    unregisterAppUpdateListener()
                }
                it.installStatus() == InstallStatus.CANCELED -> {
                    unregisterAppUpdateListener()
                }
            }
        }

        appUpdateManager.registerListener(installStateUpdatedListener)
    }

    private fun unregisterAppUpdateListener() {
        appUpdateManager.unregisterListener(installStateUpdatedListener)
    }

    private fun showAppDownloaded() {
        val snackBar = Snackbar.make(binding.root, "New app is ready!", Snackbar.LENGTH_INDEFINITE)
        snackBar.setAction("Install") {
            appUpdateManager.completeUpdate()
            unregisterAppUpdateListener()
        }
        snackBar.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IN_APP_UPDATE && resultCode != RESULT_OK) {
            showToast(this, "Update Cancelled")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)

        menu.getItem(0).subMenu.iterator().forEach {
            val drawable = it.icon
            if (drawable != null) {
                drawable.mutate()
                drawable.setColorFilter(
                    resources.getColor(R.color.grey),
                    PorterDuff.Mode.SRC_ATOP
                )
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.share -> {
                val share = Intent(Intent.ACTION_SEND)
                share.type = "text/plain"
                share.putExtra(
                    Intent.EXTRA_TEXT,
                    "To get Movies and Tv show details like casts, trailers, budget, rating etc. " +
                            "Download the app :- https://play.google.com/store/apps/details?id=$packageName"
                )
                startActivity(Intent.createChooser(share, "Share App"))
                return true
            }
            R.id.rate_us -> {
                giveReview()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        unregisterAppUpdateListener()
        super.onDestroy()
    }
}