package com.mutablestate.readonline.presentation.view

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.mutablestate.readonline.R
import com.mutablestate.readonline.presentation.activities.CameraActivity

@Composable
fun InstructionsScreen() {
    val context = LocalContext.current
    val instructionsVisibility = remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.welcome_inst_screen),
            fontSize = 30.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.help1_inst_screen),
            fontSize = 20.sp,
            textAlign = TextAlign.Justify
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.recom_inst_screen),
            fontSize = 20.sp,
            textAlign = TextAlign.Justify
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.recom1_inst_screen),
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row {
            Text(
                text = stringResource(R.string.recom2_inst_screen),
                fontSize = 14.sp,
                textAlign = TextAlign.Justify
            )
            Icon(
                painter = painterResource(id = if (instructionsVisibility.value) R.drawable.arrow_drop_down else R.drawable.arrow_drop_up), 
                contentDescription = stringResource(R.string.more_inst_inst_screen),
                modifier = Modifier.clickable { instructionsVisibility.value = !instructionsVisibility.value }
            )
        }
        
        if (instructionsVisibility.value) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pantalla_cut),
                    contentDescription = stringResource(R.string.example_image_inst_screen),
                    modifier = Modifier.rotate(270f),
                    contentScale = ContentScale.Fit
                )
                Image(
                    painter = painterResource(id = R.drawable.passport_image_crop),
                    contentDescription = stringResource(R.string.id_image_inst_screen),
                    modifier = Modifier
                        .fillMaxSize(.6f)
                        .padding(end = 40.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.recom3_inst_screen),
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.read_string_inst_screen),
            fontSize = 20.sp,
            textAlign = TextAlign.Justify
        )
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    val intent = Intent(context, CameraActivity::class.java)
                    startActivity(context, intent, null)
                }
            ) {
                Text(text = stringResource(R.string.read_button_inst_scren))
            }
        }
    }
}