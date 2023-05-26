package com.advice.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.views.Paragraph
import com.shortstack.hackertracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeOfConductScreenView(
    policy: String?,
    onBackPressed: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Code of Conduct") },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(painterResource(R.drawable.arrow_back), contentDescription = null)
                    }
                }
            )
        }) {
        if (policy != null) {
            Paragraph(
                policy,
                Modifier
                    .padding(it)
                    .verticalScroll(rememberScrollState())
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CodeOfConductScreenViewPreview() {
    ScheduleTheme {
        CodeOfConductScreenView(
            "If you have a \uD83D\uDE91 \uD83C\uDFE5 Medical or \uD83D\uDC6E Police Emergency: Call <a href=\"tel:911\">911</a> <br /><br /> If you have questions about what's happening in or around DEF CON, or otherwise need help answering a question: please visit one of the NFO (information) booths located in every DC30 venue. They're highlighted in yellow on the venue maps.  <br /><br /> If you prefer not to discuss your problem with a goon in-person (either at an Info Booth or walking around), you can reach DEF CON staff during normal hours of operation (8am to 4am) to anonymously report any behavior violating our code of conduct, or to find an empathic ear, by calling <a href=\"tel:+17252220934\">+1 (725) 222-0934</a>. <br /><br /> For relevant issues, we are collaborating with several organizations to provide expert resources for survivors, including dedicated support for LGBTQ+:<br /> - Kick at Darkness<br /> - The Rape Crisis Center Las Vegas<br /> - Nevada Coalition to End Domestic and Sexual Violence<br /> <br />",
            {}
        )
    }
}