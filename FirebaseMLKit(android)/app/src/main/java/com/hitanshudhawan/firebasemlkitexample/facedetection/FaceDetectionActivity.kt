package com.hitanshudhawan.firebasemlkitexample.facedetection

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
//import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import com.hitanshudhawan.firebasemlkitexample.R
import com.otaliastudios.cameraview.Facing
import com.otaliastudios.cameraview.Frame
import com.otaliastudios.cameraview.FrameProcessor
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_face_detection.*
import kotlinx.android.synthetic.main.content_face_detection.*

class FaceDetectionActivity : AppCompatActivity(), FrameProcessor {

    private var cameraFacing: Facing = Facing.FRONT

    private val imageView by lazy { findViewById<ImageView>(R.id.face_detection_image_view)!! }

    private lateinit var mp : MediaPlayer
    private var totalTime : Int = 0
    private var cnt: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_detection)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        face_detection_camera_view.facing = cameraFacing
        face_detection_camera_view.setLifecycleOwner(this)
        face_detection_camera_view.addFrameProcessor(this)

        mp = MediaPlayer.create(this, R.raw.detection)
        mp.isLooping = true
        mp.setVolume(1.0f, 1.0f)
        mp.start()
        mp.pause()
    }

    override fun process(frame: Frame) {

        val width = frame.size.width
        val height = frame.size.height

        val metadata = FirebaseVisionImageMetadata.Builder()
                .setWidth(width)
                .setHeight(height)
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setRotation(if (cameraFacing == Facing.FRONT) FirebaseVisionImageMetadata.ROTATION_270 else FirebaseVisionImageMetadata.ROTATION_90)
                .build()

        val firebaseVisionImage = FirebaseVisionImage.fromByteArray(frame.data, metadata)
        val options = FirebaseVisionFaceDetectorOptions.Builder()
                .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS).setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .build()
        val faceDetector = FirebaseVision.getInstance().getVisionFaceDetector(options)
        faceDetector.detectInImage(firebaseVisionImage)
                .addOnSuccessListener {
                    face_detection_camera_image_view.setImageBitmap(null)


                    val bitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    val dotPaint = Paint()
                    dotPaint.color = Color.RED
                    dotPaint.style = Paint.Style.FILL
                    dotPaint.strokeWidth = 4F
                    val linePaint = Paint()
                    linePaint.color = Color.GREEN
                    linePaint.style = Paint.Style.STROKE
                    linePaint.strokeWidth = 2F

                    showProgress()


                    for (face in it) {

                        val faceContours = face.getContour(FirebaseVisionFaceContour.FACE).points
                        for ((i, contour) in faceContours.withIndex()) {
                            if (i != faceContours.lastIndex)
                                canvas.drawLine(contour.x, contour.y, faceContours[i + 1].x, faceContours[i + 1].y, linePaint)
                            else
                                canvas.drawLine(contour.x, contour.y, faceContours[0].x, faceContours[0].y, linePaint)
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }

                        val leftEyebrowTopContours = face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_TOP).points
                        for ((i, contour) in leftEyebrowTopContours.withIndex()) {
                            if (i != leftEyebrowTopContours.lastIndex)
                                canvas.drawLine(contour.x, contour.y, leftEyebrowTopContours[i + 1].x, leftEyebrowTopContours[i + 1].y, linePaint)
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }

                        val leftEyebrowBottomContours = face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_BOTTOM).points
                        for ((i, contour) in leftEyebrowBottomContours.withIndex()) {
                            if (i != leftEyebrowBottomContours.lastIndex)
                                canvas.drawLine(contour.x, contour.y, leftEyebrowBottomContours[i + 1].x, leftEyebrowBottomContours[i + 1].y, linePaint)
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }

                        val rightEyebrowTopContours = face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_TOP).points
                        for ((i, contour) in rightEyebrowTopContours.withIndex()) {
                            if (i != rightEyebrowTopContours.lastIndex)
                                canvas.drawLine(contour.x, contour.y, rightEyebrowTopContours[i + 1].x, rightEyebrowTopContours[i + 1].y, linePaint)
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }

                        val rightEyebrowBottomContours = face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_BOTTOM).points
                        for ((i, contour) in rightEyebrowBottomContours.withIndex()) {
                            if (i != rightEyebrowBottomContours.lastIndex)
                                canvas.drawLine(contour.x, contour.y, rightEyebrowBottomContours[i + 1].x, rightEyebrowBottomContours[i + 1].y, linePaint)
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }


                        val leftEyeContours = face.getContour(FirebaseVisionFaceContour.LEFT_EYE).points
                        for ((i, contour) in leftEyeContours.withIndex()) {
                            if (i != leftEyeContours.lastIndex)
                                canvas.drawLine(contour.x, contour.y, leftEyeContours[i + 1].x, leftEyeContours[i + 1].y, linePaint)
                            else
                                canvas.drawLine(contour.x, contour.y, leftEyeContours[0].x, leftEyeContours[0].y, linePaint)
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)



                        }

                        val rightEyeContours = face.getContour(FirebaseVisionFaceContour.RIGHT_EYE).points
                        for ((i, contour) in rightEyeContours.withIndex()) {
                            if (i != rightEyeContours.lastIndex)
                                canvas.drawLine(contour.x, contour.y, rightEyeContours[i + 1].x, rightEyeContours[i + 1].y, linePaint)
                            else
                                canvas.drawLine(contour.x, contour.y, rightEyeContours[0].x, rightEyeContours[0].y, linePaint)
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }


                        val upperLipTopContours = face.getContour(FirebaseVisionFaceContour.UPPER_LIP_TOP).points
                        for ((i, contour) in upperLipTopContours.withIndex()) {
                            if (i != upperLipTopContours.lastIndex)
                                canvas.drawLine(contour.x, contour.y, upperLipTopContours[i + 1].x, upperLipTopContours[i + 1].y, linePaint)
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }

                        val upperLipBottomContours = face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).points
                        for ((i, contour) in upperLipBottomContours.withIndex()) {
                            if (i != upperLipBottomContours.lastIndex)
                                canvas.drawLine(contour.x, contour.y, upperLipBottomContours[i + 1].x, upperLipBottomContours[i + 1].y, linePaint)
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }

                        val lowerLipTopContours = face.getContour(FirebaseVisionFaceContour.LOWER_LIP_TOP).points
                        for ((i, contour) in lowerLipTopContours.withIndex()) {
                            if (i != lowerLipTopContours.lastIndex)
                                canvas.drawLine(contour.x, contour.y, lowerLipTopContours[i + 1].x, lowerLipTopContours[i + 1].y, linePaint)
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }

                        val lowerLipBottomContours = face.getContour(FirebaseVisionFaceContour.LOWER_LIP_BOTTOM).points
                        for ((i, contour) in lowerLipBottomContours.withIndex()) {
                            if (i != lowerLipBottomContours.lastIndex)
                                canvas.drawLine(contour.x, contour.y, lowerLipBottomContours[i + 1].x, lowerLipBottomContours[i + 1].y, linePaint)
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }

                        val noseBridgeContours = face.getContour(FirebaseVisionFaceContour.NOSE_BRIDGE).points
                        for ((i, contour) in noseBridgeContours.withIndex()) {
                            if (i != noseBridgeContours.lastIndex)
                                canvas.drawLine(contour.x, contour.y, noseBridgeContours[i + 1].x, noseBridgeContours[i + 1].y, linePaint)
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }

                        val noseBottomContours = face.getContour(FirebaseVisionFaceContour.NOSE_BOTTOM).points
                        for ((i, contour) in noseBottomContours.withIndex()) {
                            if (i != noseBottomContours.lastIndex)
                                canvas.drawLine(contour.x, contour.y, noseBottomContours[i + 1].x, noseBottomContours[i + 1].y, linePaint)
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }
                        //Thread.sleep(100)

                        if (face.leftEyeOpenProbability !=
                                FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                            val leftEyeOpenProb = face.leftEyeOpenProbability

                            if (face.rightEyeOpenProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                val rightEyeOpenProb = face.rightEyeOpenProbability

                                // if (leftEyeOpenProb >= 0 && rightEyeOpenProb >= 0)
                                if (leftEyeOpenProb < 0.3 && rightEyeOpenProb < 0.3) {
                                    cnt++
                                    if (cnt > 2)
                                        mp.start()
                                }
                                else if (leftEyeOpenProb > 0.3 || rightEyeOpenProb > 0.3) {
                                    mp.pause()
                                    cnt = 0
                                }

                        }
                        }

                        if (cameraFacing == Facing.FRONT) {
                            val matrix = Matrix()
                            matrix.preScale(-1F, 1F)
                            val flippedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                            face_detection_camera_image_view.setImageBitmap(flippedBitmap)
                        } else {
                            face_detection_camera_image_view.setImageBitmap(bitmap)
                        }
                    }

                }
                .addOnFailureListener {
                    face_detection_camera_image_view.setImageBitmap(null)
                }


    }


    private fun showProgress(){
        findViewById<View>(R.id.content_face_detection_text_view1).visibility = View.GONE
        findViewById<View>(R.id.content_face_detection_text_view1).visibility = View.VISIBLE
    }
}