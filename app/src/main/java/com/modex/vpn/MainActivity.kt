package com.modex.vpn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.modex.vpn.ui.components.ModexConnectButton
import com.modex.vpn.ui.components.VpnState
import com.modex.vpn.ui.theme.ModexVPNTheme
import com.modex.vpn.ui.theme.PureBlack
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ModexVPNTheme {
                var vpnState by remember { mutableStateOf(VpnState.Disconnected) }
                val coroutineScope = rememberCoroutineScope()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PureBlack),
                    contentAlignment = Alignment.Center
                ) {
                    ModexConnectButton(
                        vpnState = vpnState,
                        onClick = {
                            when (vpnState) {
                                VpnState.Disconnected -> {
                                    vpnState = VpnState.Connecting
                                    coroutineScope.launch {
                                        delay(2000)
                                        if (vpnState == VpnState.Connecting) {
                                            vpnState = VpnState.Connected
                                        }
                                    }
                                }
                                VpnState.Connecting -> {
                                    vpnState = VpnState.Disconnected
                                }
                                VpnState.Connected -> {
                                    vpnState = VpnState.Disconnected
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
