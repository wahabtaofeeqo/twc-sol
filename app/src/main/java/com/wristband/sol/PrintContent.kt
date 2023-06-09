package com.wristband.sol
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.gprinter.command.LabelCommand
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Vector


/**
 * Copyright (C), 2012-2020, 珠海佳博科技股份有限公司
 * FileName: PrintConntent
 * Author: Circle
 * Date: 2020/7/20 10:04
 * Description: 打印内容
 */
object PrintContent {
    /**
     * 标签打印测试页
     *
     * @return
     */
    fun getLabel(context: Context, content: String, counter: Int): Vector<Byte> {

        val tsc = LabelCommand()

        // 设置标签尺寸宽高，按照实际尺寸设置 单位mm
        tsc.addUserCommand("\r\n")
        tsc.addSize(25, 275)

        // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0 单位mm
        tsc.addGap(0)
        //设置纸张类型为黑标，发送BLINE 指令不能同时发送GAP指令

        // 设置打印方向
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL)

        // 设置原点坐标
        tsc.addReference(0, 0)

        //设置浓度
        tsc.addDensity(LabelCommand.DENSITY.DNESITY4)

        // 撕纸模式开启
        tsc.addTear(LabelCommand.RESPONSE_MODE.ON)

        // 清除打印缓冲区
        tsc.addCls()

        // Company
        tsc.addText(
            20, 1000,
            LabelCommand.FONTTYPE.Bold,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_1,
            LabelCommand.FONTMUL.MUL_1,
            "SolBeach"
        )

        // Number
        tsc.addText(
            20, 1100,
            LabelCommand.FONTTYPE.Bold,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_1,
            LabelCommand.FONTMUL.MUL_1,
            "# $counter"
        )

        // Date
        val todayDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
        tsc.addText(
            20, 1150,
            LabelCommand.FONTTYPE.Bold,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_1,
            LabelCommand.FONTMUL.MUL_1,
            todayDate
        )

        //打印繁体
        // tsc.addUnicodeText(30,50, LabelCommand.FONTTYPE.TRADITIONAL_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"BIG5碼繁體中文","BIG5");

        //打印韩文
        // tsc.addUnicodeText(30,80, LabelCommand.FONTTYPE.KOREAN, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"Korean 지아보 하성","EUC_KR");

        //英数字
//        tsc.addText(240,20, LabelCommand.FONTTYPE.FONT_1, LabelCommand.ROTATION.ROTATION_0,LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"1");
//        tsc.addText(250,20, LabelCommand.FONTTYPE.FONT_2, LabelCommand.ROTATION.ROTATION_0,LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"2");
//        tsc.addText(270,20, LabelCommand.FONTTYPE.FONT_3, LabelCommand.ROTATION.ROTATION_0,LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"3");
//        tsc.addText(300,20, LabelCommand.FONTTYPE.FONT_4, LabelCommand.ROTATION.ROTATION_0,LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"4");
//        tsc.addText(330,20, LabelCommand.FONTTYPE.FONT_5, LabelCommand.ROTATION.ROTATION_0,LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"5");
//        tsc.addText(240,40, LabelCommand.FONTTYPE.FONT_6, LabelCommand.ROTATION.ROTATION_0,LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"6");
//        tsc.addText(250,40, LabelCommand.FONTTYPE.FONT_7, LabelCommand.ROTATION.ROTATION_0,LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"7");
//        tsc.addText(270,40, LabelCommand.FONTTYPE.FONT_8, LabelCommand.ROTATION.ROTATION_0,LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"8");
//        tsc.addText(300,60, LabelCommand.FONTTYPE.FONT_9, LabelCommand.ROTATION.ROTATION_0,LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"9");
//        tsc.addText(330,80, LabelCommand.FONTTYPE.FONT_10, LabelCommand.ROTATION.ROTATION_0,LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"10");

        // 绘制图片
        //val b = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_priter)
        // tsc.drawImage(30, 100, 300, b);
        //val b2 = BitmapFactory.decodeResource(context.resources, R.drawable.flower)
        //tsc.drawJPGImage(200,250,200, b2);

        //绘制二维码
        tsc.addQRCode(20, 1300,
            LabelCommand.EEC.LEVEL_L, 7,
            LabelCommand.ROTATION.ROTATION_0, content)

        // 绘制一维条码
        //tsc.add1DBarcode(30, 380, LabelCommand.BARCODETYPE.CODE128, 80, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, "12345678");

        // 打印标签
        tsc.addPrint(1, 1)

        // 打印标签后 蜂鸣器响
        tsc.addSound(2, 100)

        //开启钱箱
        tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255)

        // 发送数据
        return tsc.command
    }

    /**
     * 获取图片
     * @param context
     * @return
     */
    fun getBitmap(context: Context?): Bitmap? {
//        val v = View.inflate(context, R.layout.page, null)
//        val tableLayout = v.findViewById<View>(R.id.line) as TableLayout
//        val total = v.findViewById<View>(R.id.total) as TextView
//        val cashier = v.findViewById<View>(R.id.cashier) as TextView
//        tableLayout.addView(ctv(context, "红茶\n加热\n加糖", 3, 8))
//        tableLayout.addView(ctv(context, "绿茶", 899, 109))
//        tableLayout.addView(ctv(context, "咖啡", 4, 15))
//        tableLayout.addView(ctv(context, "红茶", 3, 8))
//        tableLayout.addView(ctv(context, "绿茶", 8, 10))
//        tableLayout.addView(ctv(context, "咖啡", 4, 15))
//        total.text = "998"
//        cashier.text = "张三"
//        return convertViewToBitmap(v)

        return null
    }

    /**
     * mxl转bitmap图片
     * @return
     */
    fun convertViewToBitmap(view: View): Bitmap {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            ),
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            )
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.buildDrawingCache()
        return view.drawingCache
    }

    fun ctv(context: Context?, name: String?, k: Int, n: Int): TableRow {
        val tb = TableRow(context)
        tb.layoutParams =
            TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
        val tv1 = TextView(context)
        tv1.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        tv1.text = name
        tv1.setTextColor(Color.BLACK)
        tb.addView(tv1)
        val tv2 = TextView(context)
        tv2.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        tv2.text = k.toString() + ""
        tv2.setTextColor(Color.BLACK)
        tb.addView(tv2)
        val tv3 = TextView(context)
        tv3.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        tv3.text = n.toString() + ""
        tv3.setTextColor(Color.BLACK)
        tb.addView(tv3)
        return tb
    }

    /**
     * 获取Assets文件
     * @param fileName
     * @return
     */
    fun getFromAssets(context: Context, fileName: String?): String {
        var result = ""
        try {
            val inputReader = InputStreamReader(
                context.resources.assets.open(
                    fileName!!
                )
            )
            val bufReader = BufferedReader(inputReader)
            var line = ""
            while (bufReader.readLine().also { line = it } != null) result += """
     $line
     
     """.trimIndent()
            return result
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun getXmlBitmap(context: Context?): Vector<Byte> {
        val tsc = LabelCommand()
        // 设置标签尺寸宽高，按照实际尺寸设置 单位mm
        tsc.addUserCommand("\r\n")
        tsc.addSize(58, 100)
        // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0 单位mm
        tsc.addGap(0)
        // 设置打印方向
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL)
        // 设置原点坐标
        tsc.addReference(0, 0)
        //设置浓度
        tsc.addDensity(LabelCommand.DENSITY.DNESITY4)
        // 撕纸模式开启
        tsc.addTear(LabelCommand.RESPONSE_MODE.ON)
        // 清除打印缓冲区
        tsc.addCls()
        val bitmap = getBitmap(context)
        // 绘制图片
        /**
         * x:打印起始横坐标
         * y:打印起始纵坐标
         * mWidth：打印宽度以dot为单位
         * nbitmap：源图
         */
        tsc.drawXmlImage(10, 10, bitmap!!.width, bitmap)
        // 打印标签
        tsc.addPrint(1, 1)
        return tsc.command
    }
}
