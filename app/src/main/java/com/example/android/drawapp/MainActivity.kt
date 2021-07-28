package com.example.android.drawapp

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.brushsize_bg.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

private var mImageButton:ImageButton?=null
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        undo.setOnClickListener {
            drawingView.undoPath()
        }

        import_image.setOnClickListener {
            if(isReadStorageIsALLOWED()){
                var pickImage= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickImage, GALLERY)

            }
            else{
                RequestAccessStorage()

            }

        }

        store_image.setOnClickListener {
            if(isReadStorageIsALLOWED()){
                BitMapAsyncTask(toStoreImages(frame_layout)).execute()
            }
            else{
                RequestAccessStorage()
            }

        }
        brush_paint.setOnClickListener { showBrushDialog() }
mImageButton=ll[1] as ImageButton
        mImageButton!!.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.selected_palate))
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            if(requestCode== GALLERY){
                try{
                    if(data!!.data!=null){
                        image.visibility=View.VISIBLE
                        image.setImageURI(data.data)
                    }
                    else{
                        Toast.makeText(this,"Oops you have selected no image!!",Toast.LENGTH_SHORT).show()
                    }

                }
                catch(e:Exception){
                    e.printStackTrace()
                }
            }
        }
    }



    fun paintClicked(view:View){
        if(view!== mImageButton){
            val imageButton1=view as ImageButton
            val colorTag=imageButton1.tag.toString()
            drawingView.setColor(colorTag)
            imageButton1.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.selected_palate))
            mImageButton!!.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.normal_palate))

            mImageButton=view

        }
    }

companion object {
    var ACCESS_TO_GALLERY=1
  var  GALLERY=2

}
    private fun isReadStorageIsALLOWED():Boolean{
        val result=ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
        return result==PackageManager.PERMISSION_GRANTED
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== ACCESS_TO_GALLERY){
            if(grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission granted",Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(this,"Oops!you have denied for the access",Toast.LENGTH_SHORT).show()
        }



    }fun RequestAccessStorage(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE).toString())){
            Toast.makeText(this,"You have a granted access to storage",Toast.LENGTH_SHORT).show()
        }
        else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),
                ACCESS_TO_GALLERY)
        }
    }

    fun toStoreImages(view: View):Bitmap{
        val returnBitmap=Bitmap.createBitmap(view.height,view.width,Bitmap.Config.ARGB_8888)
        val canvas=Canvas(returnBitmap)
        val bgDrawable =view.background
        if(bgDrawable!=null){
            bgDrawable.draw(canvas)

        }
        else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return returnBitmap
    }

    private inner class BitMapAsyncTask(val mBitmap: Bitmap):AsyncTask<Any,Void,String>(){
        override fun doInBackground(vararg params: Any?): String {

            var result=""
            if(mBitmap !=null){


                try {
                   val bytes=ByteArrayOutputStream()
                    mBitmap.compress(Bitmap.CompressFormat.PNG,90,bytes)
                    val f= File(externalCacheDir!!.absoluteFile.toString() + File.separator + "DrawApp" +System.currentTimeMillis()/1000 +".png")
                    val fos=FileOutputStream(f)
                    fos.write(bytes.toByteArray())
fos.close()
                    result=f.absolutePath

                }
                catch (e:Exception){
                    e.printStackTrace()
                }


            }

return result


        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(!result!!.isEmpty()){
                Toast.makeText(this@MainActivity,"Operation is successfully completed:$result",Toast.LENGTH_SHORT).show()

            }
            else{
                Toast.makeText(this@MainActivity,"Sorry!Something went wrong",Toast.LENGTH_SHORT).show()


            }
            MediaScannerConnection.scanFile(this@MainActivity, arrayOf(result),null){
                paths,uri->val shareContent=Intent()
                shareContent.action=Intent.ACTION_SEND
                shareContent.putExtra(Intent.EXTRA_STREAM,uri)
                shareContent.type="image/png"
                startActivity(
                    Intent.createChooser(shareContent,"Share")
                )

            }
        }




    }













fun showBrushDialog(){
    val brushDialog=Dialog(this)
    brushDialog.setContentView(R.layout.brushsize_bg)
    brushDialog.setTitle("Brush size")
    val small_button=brushDialog.small_size
    small_button.setOnClickListener {
        drawingView.setUpBrushSize(10.toFloat())
        brushDialog.dismiss()
    }
    val medium_button=brushDialog.medium_size
    medium_button.setOnClickListener {
        drawingView.setUpBrushSize(20.toFloat())
        brushDialog.dismiss()
    }
    val large_button=brushDialog.large_size
    large_button.setOnClickListener {
        drawingView.setUpBrushSize( 30.toFloat())
        brushDialog.dismiss()
    }
    val extraL_button=brushDialog.extra_large_size
    extraL_button .setOnClickListener {
        drawingView.setUpBrushSize(40.toFloat())
        brushDialog.dismiss()
    }
    brushDialog.show()
}
}