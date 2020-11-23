package com.ag.smartsens

import android.bluetooth.*
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_ble_details.*
import kotlinx.android.synthetic.main.activity_main.simulation
import java.util.*
import kotlin.IntArray as IntArray


class BleDetails : AppCompatActivity() {

    private var bleActif = false
    private var bluetoothGatt: BluetoothGatt? = null
    var notifier = false
    lateinit var tSeries: LineGraphSeries<DataPoint>
    private var graphLastXValue = -1.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ble_details)

        val device: BluetoothDevice? = intent.getParcelableExtra("ble_device")
        //device_name.text = device.name ?: "Unnamed"
        bluetoothGatt = device?.connectGatt(this, false, gattCallback)

        /*
        disconnect_button.setOnClickListener {
            BluetoothProfile.STATE_DISCONNECTED;
            device_statut.text = STATE_DISCONNECTED
            bluetoothGatt?.close()
            Log.i(TAG, "Disconnected from GATT server.")

        }
         */

        simulation.setOnClickListener {
            if (!notifier) {
                notifier = true
                if (bluetoothGatt != null) {
                    setCharacteristicNotificationInternal(
                        bluetoothGatt,
                        bluetoothGatt?.services?.get(2)?.characteristics?.get(1),
                        true
                    )
                }
            } else {
                notifier = false
                if (bluetoothGatt != null) {
                    setCharacteristicNotificationInternal(
                        bluetoothGatt, bluetoothGatt?.services?.get(
                            2
                        )?.characteristics?.get(1), false
                    )
                }
            }
        }

        tSeries = LineGraphSeries()

        graph.addSeries(tSeries)
        graph.viewport.isYAxisBoundsManual = true
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinY(0.0)
        graph.viewport.setMaxY(50.0)
        graph.viewport.setMinX(0.0)
        graph.viewport.setMaxX(40.0)

        // Légende
        tSeries.title = "Temperature"
        graph.legendRenderer.isVisible = true
        graph.legendRenderer.align = LegendRenderer.LegendAlign.TOP
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(
            gatt: BluetoothGatt,
            status: Int,
            newState: Int
        ) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    runOnUiThread {
                        //device_statut.text = STATE_CONNECTED
                    }

                    bluetoothGatt?.discoverServices()
                    Log.i("TAG", "Connected to GATT server.")
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    runOnUiThread {
                        //device_statut.text = STATE_DISCONNECTED
                    }
                    Log.i("TAG", "Disconnected from GATT server.")
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            val value = characteristic.getStringValue(0)

            runOnUiThread {
                //text_view.append("$value\n")
            }

            Log.e(
                "TAG",
                "onCharacteristicRead: " + value + " UUID " + characteristic.uuid.toString()
            )

        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            val value = String(characteristic.value)
            runOnUiThread {
                //text_view.append("$value\n")
            }
            Log.e(
                "TAG",
                "onCharacteristicWrite: " + value + " UUID " + characteristic.uuid.toString()
            )

        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic
        ) {
            val hex =
                characteristic.value.joinToString("") { byte -> "%02x".format(byte) }.toUpperCase(
                    Locale.FRANCE
                )

            val hexTemp = hex.subSequence(0, 4)
            val temp = hexTemp.toString()

            runOnUiThread {
                graphLastXValue += 1.0
                tSeries.appendData(DataPoint(graphLastXValue, hexToTemp(temp)), true, 40)
            }
            Log.e(
                "TAG",
                "onCharacteristicChanged: $temp ${hexToTemp(temp)} UUID ${characteristic.uuid} x : "
            )
        }
    }

    private fun setCharacteristicNotificationInternal(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        enabled: Boolean
    ) {
        gatt?.setCharacteristicNotification(characteristic, enabled)

        if (characteristic != null) {
            if (characteristic.descriptors.size > 0) {

                val descriptors = characteristic.descriptors
                for (descriptor in descriptors) {

                    if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0) {
                        descriptor.value =
                            if (enabled) BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE else BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                    } else if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_INDICATE != 0) {
                        descriptor.value =
                            if (enabled) BluetoothGattDescriptor.ENABLE_INDICATION_VALUE else BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                    }
                    gatt?.writeDescriptor(descriptor)
                }
            }
        }
    }

    private fun hexToTemp(hexTemp: String): Double {
        val i = hexTemp.toInt(16)
        val j = Integer.toBinaryString(i).padStart(16, '0')
        val tab = test(j)

        var temp =
            tab[1] * 64 + tab[2] * 32 + tab[3] * 16 + tab[4] * 8 + tab[5] * 4 +
                    tab[6] * 2 + tab[7] * 1 + tab[8] * 0.5 + tab[9] * 0.25 +
                    tab[10] * 0.125 + tab[11] * 0.0625

        return if (tab[0] == 1) {
            -temp
        } else {
            temp
        }
    }

    private fun cti(i: Char): Int {
        return if (i.toString() != "0") 1 else 0
    }

    fun test(chaine: String): MutableList<Int> {
        var tab: MutableList<Int> = arrayListOf()
        for (element in chaine) {
            tab.add(cti(element))
        }
        return tab
    }
}