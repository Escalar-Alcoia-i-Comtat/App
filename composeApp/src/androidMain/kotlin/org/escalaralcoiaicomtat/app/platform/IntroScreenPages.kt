package org.escalaralcoiaicomtat.app.platform

import android.app.GrammaticalInflectionManager
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import org.escalaralcoiaicomtat.android.R
import org.escalaralcoiaicomtat.android.applicationContext
import org.escalaralcoiaicomtat.app.ui.reusable.Icon
import org.escalaralcoiaicomtat.app.ui.reusable.IntroPage

actual object IntroScreenPages {
    actual val pages: Array<@Composable () -> Unit> = listOfNotNull<@Composable () -> Unit>(
        if (Build.VERSION.SDK_INT >= 34) {
            {
                GenderSelectionIntroPage()
            }
        } else null
    ).toTypedArray()

    @Composable
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun GenderSelectionIntroPage() {
        IntroPage(
            // TODO: Fix icon
            icon = Icon(painterResource(R.drawable.man_raising_hand)),
            title = stringResource(R.string.intro_0_title),
            message = stringResource(R.string.intro_0_message),
            options = object : IntroPage.Options<Int>() {
                private val grammaticalInflectionManager by lazy {
                    applicationContext.getSystemService(GrammaticalInflectionManager::class.java)
                }

                override val values: Map<Int, String> = mapOf(
                    Configuration.GRAMMATICAL_GENDER_NEUTRAL to applicationContext.getString(R.string.gender_neuter),
                    Configuration.GRAMMATICAL_GENDER_MASCULINE to applicationContext.getString(R.string.gender_masculine),
                    Configuration.GRAMMATICAL_GENDER_FEMININE to applicationContext.getString(R.string.gender_feminine),
                )

                override val defaultIndex: Int = grammaticalInflectionManager
                    .applicationGrammaticalGender
                    .let {
                        when (it) {
                            Configuration.GRAMMATICAL_GENDER_MASCULINE -> 1
                            Configuration.GRAMMATICAL_GENDER_FEMININE -> 2
                            else -> 0
                        }
                    }

                override val label: @Composable () -> String = {
                    stringResource(R.string.choose)
                }

                override fun onSelected(key: Int): Boolean {
                    grammaticalInflectionManager.setRequestedApplicationGrammaticalGender(
                        key
                    )

                    return true
                }
            }
        )
    }
}
