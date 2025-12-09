package com.example.smart.sportlive.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.smart.sportlive.R

@Composable
fun translateSportName(englishName: String): String {
    return when (englishName.lowercase()) {
        "football", "soccer" -> stringResource(R.string.sport_football)
        "basketball" -> stringResource(R.string.sport_basketball)
        "tennis" -> stringResource(R.string.sport_tennis)
        "hockey" -> stringResource(R.string.sport_hockey)
        "volleyball" -> stringResource(R.string.sport_volleyball)
        "handball" -> stringResource(R.string.sport_handball)
        "baseball" -> stringResource(R.string.sport_baseball)
        "american football" -> stringResource(R.string.sport_american_football)
        "ice hockey" -> stringResource(R.string.sport_ice_hockey)
        "table tennis", "ping pong" -> stringResource(R.string.sport_table_tennis)
        "badminton" -> stringResource(R.string.sport_badminton)
        "cricket" -> stringResource(R.string.sport_cricket)
        "rugby" -> stringResource(R.string.sport_rugby)
        "golf" -> stringResource(R.string.sport_golf)
        "boxing" -> stringResource(R.string.sport_boxing)
        "mma", "mixed martial arts" -> stringResource(R.string.sport_mma)
        "esports", "e-sports" -> stringResource(R.string.sport_esports)
        "darts" -> stringResource(R.string.sport_darts)
        "snooker" -> stringResource(R.string.sport_snooker)
        "waterpolo", "water polo" -> stringResource(R.string.sport_waterpolo)
        else -> englishName // Return original if no translation found
    }
}

