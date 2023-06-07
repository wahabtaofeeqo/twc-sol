package com.wristband.sol

import android.util.Log
import com.gprinter.bean.PrinterDevices
import com.gprinter.io.BluetoothPort
import com.gprinter.io.EthernetPort
import com.gprinter.io.PortManager
import com.gprinter.io.SerialPort
import com.gprinter.io.UsbPort
import com.gprinter.utils.Command
import com.gprinter.utils.ConnMethod
import java.io.IOException
import java.util.Vector
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class Printer {

    //val devices: PrinterDevices? = null
    companion object {
        private var printer: Printer? = null

        /**
         * 获取打印机管理类
         * @return
         */
        var portManager: PortManager? = null

        /**
         * 单例
         * @return
         */
        val instance: Printer
            get() {
                if (printer == null) {
                    printer = Printer()
                }
                return printer as Printer
            }

        /**
         * 获取连接状态
         * @return
         */
        val connectState: Boolean
            get() = portManager!!.connectStatus

        /**
         * 连接
         * @param devices
         */
        fun connect(devices: PrinterDevices) {
            val workerPool: ExecutorService = Executors.newSingleThreadExecutor()
            workerPool.submit(kotlinx.coroutines.Runnable {
                if (portManager != null) {
                    portManager!!.closePort()
                    try {
                        Thread.sleep(2000)
                    } catch (_: InterruptedException) { }
                }

                when (devices.connMethod) {
                    ConnMethod.BLUETOOTH -> {
                        portManager = BluetoothPort(devices)
                        portManager!!.openPort()
                    }

                    ConnMethod.USB -> {
                        portManager = UsbPort(devices)
                        portManager!!.openPort()
                    }

                    ConnMethod.WIFI -> {
                        portManager = EthernetPort(devices)
                        portManager!!.openPort()
                    }

                    ConnMethod.SERIALPORT -> {
                        portManager = SerialPort(devices)
                        portManager!!.openPort()
                    }

                    else -> {}
                }
            })
        }

        /**
         * 发送数据到打印机 字节数据
         * @param vector
         * @return true发送成功 false 发送失败
         * 打印机连接异常或断开发送时会抛异常，可以捕获异常进行处理
         */
        @Throws(IOException::class)
        fun sendDataToPrinter(vector: ByteArray?): Boolean {
            return if (portManager == null) {
                false
            } else portManager!!.writeDataImmediately(vector)
        }

        /**
         * 获取打印机状态
         * @param printerCommand 打印机命令 ESC为小票，TSC为标签 ，CPCL为面单
         * @return 返回值常见文档说明
         * @throws IOException
         */
        @Throws(IOException::class)
        fun getPrinterState(printerCommand: Command?, delayMillis: Long): Int {
            return portManager!!.getPrinterStatus(printerCommand)
        }

        /**
         * 获取打印机电量
         * @return
         * @throws IOException
         */
        @get:Throws(IOException::class)
        val power: Int
            get() = portManager!!.power
        /**
         * 获取打印机指令
         * @return
         */
        /**
         * 设置使用指令
         * @param printerCommand
         */
        var printerCommand: Command?
            get() = portManager!!.command
            set(printerCommand) {
                if (portManager == null) {
                    return
                }
                portManager!!.command = printerCommand
            }

        /**
         * 发送数据到打印机 指令集合内容
         * @param vector
         * @return true发送成功 false 发送失败
         * 打印机连接异常或断开发送时会抛异常，可以捕获异常进行处理
         */
        @Throws(IOException::class)
        fun sendDataToPrinter(vector: Vector<Byte?>?): Boolean {
            return if (portManager == null) {
                false
            } else portManager!!.writeDataImmediately(vector)
        }

        /**
         * 关闭连接
         * @return
         */
        fun close() {
            if (portManager != null) {
                portManager!!.closePort()
                portManager = null
            }
        }
    }
}
