package com.example.editalbum;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

/**
 * 配置アイテムクラス
 * @author kohsaku
 *
 */
public class PlacedItem {

	/**
	 * 選択されているか
	 */
	public boolean isSelected;

	/**
	 * 画像を読み込むか
	 */
	public boolean isForceLoadBitmap;

	/**
	 * アクティビティ
	 */
	private EditAlbumActivity mActivity;

	/**
	 * ビットマップ
	 */
	private Bitmap mBitmap;

	/**
	 * 幅
	 */
	public float width = 0.0f;

	/**
	 * 高さ
	 */
	public float height = 0.0f;

	/**
	 * 縦横の割合
	 */
	private float mRatio = 1.0f;

	/**
	 * 中心点
	 */
	public PointF center;

	/**
	 * 左上の点
	 */
	private PointF mLeftTop;

	/**
	 * 左下の点
	 */
	private PointF mLeftBottom;

	/**
	 * 右上の点
	 */
	private PointF mRightTop;

	/**
	 * 右下の点
	 */
	private PointF mRightBottom;

	/**
	 * 右上の画像
	 */
	private Bitmap mEditRotateRT;

	/**
	 * 左上の画像
	 */
	private Bitmap mEditSizeLT;

	/**
	 * 左下の画像
	 */
	private Bitmap mEditSizeLB;

	/**
	 * 右下の画像
	 */
	private Bitmap mEditSizeRB;

	/**
	 * 画像のMatrix
	 */
	private Matrix mBitmapMatrix;

	/**
	 * 編集ボタンのMatrix
	 */
	private Matrix mEditButtonMatrix;

	/**
	 * ターゲットのビットマップ
	 */
	public Bitmap mTargetBitmap;

	/**
	 * 画像左のX座標
	 */
	private float mLeft;

	/**
	 * 画像右のX座標
	 */
	private float mRight;

	/**
	 * 画像上のY座標
	 */
	private float mTop;

	/**
	 * 画像下のY座標
	 */
	private float mBottom;

	/**
	 * 向き
	 */
	private int mDirection;

	/**
	 * コンストラクタ
	 *
	 * @param editAlbumActivity
	 * @param itemBitmap
	 */
	public PlacedItem(EditAlbumActivity editAlbumActivity, Bitmap itemBitmap) {
		mActivity = editAlbumActivity;
		setBitmap(itemBitmap);
		init();
	}

	/**
	 * Bitmapを設定する
	 * @param bitmap
	 */
	private void setBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			if (mBitmap != null) {
				mBitmap = null;
			}
			mBitmap = bitmap;
			width = mBitmap.getWidth();
			height = mBitmap.getHeight();
			mRatio = (height / width);
		}
	}

	/**
	 * 初期化
	 */
	private void init() {
		center = new PointF(0.0f, 0.0f);
		mEditRotateRT = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.rotate);
		mEditSizeLT = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.resize2);
		mEditSizeLB = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.resize1);
		mEditSizeRB = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.resize2);
		mBitmapMatrix = new Matrix();
		mEditButtonMatrix = new Matrix();
		mEditButtonMatrix.postTranslate(-mEditSizeLT.getWidth() / 2, -mEditSizeLT.getHeight() / 2);
		updateItem();
	}

	/**
	 * 描写処理
	 * @param canvas
	 */
	public void drawInCanvas(Canvas canvas) {
		updateItem();
		RectF rectF = null;
		if(mBitmap != null) {
			if ((isSelected) || (mTargetBitmap == null) || (isForceLoadBitmap)) {
				loadTargetBitmap();
			}
			float x = center.x - mTargetBitmap.getWidth() / 2;
			float y = center.y - mTargetBitmap.getHeight() / 2;
			rectF = new RectF(x, y, x + mTargetBitmap.getWidth(), y + mTargetBitmap.getHeight());
		}
		canvas.drawBitmap(mTargetBitmap, new Rect(0, 0, mTargetBitmap.getWidth(), mTargetBitmap.getHeight()), rectF, null);
		isForceLoadBitmap = false;
		if(isSelected) {
			Paint paint = new Paint();
			paint.setStrokeWidth(1.0f);
			paint.setAntiAlias(true);
			paint.setColor(Color.rgb(250, 53, 153));
			// 線を描く
			canvas.drawLine(mLeftTop.x, mLeftTop.y, mRightTop.x, mRightTop.y, paint);
			canvas.drawLine(mRightTop.x, mRightTop.y, mRightBottom.x, mRightBottom.y, paint);
			canvas.drawLine(mRightBottom.x, mRightBottom.y, mLeftBottom.x, mLeftBottom.y, paint);
			canvas.drawLine(mLeftBottom.x, mLeftBottom.y, mLeftTop.x, mLeftTop.y, paint);

			// 四隅のボタンの設定
			Matrix rightTopMatrix = new Matrix(this.mEditButtonMatrix);
			rightTopMatrix.postTranslate(mRightTop.x, mRightTop.y);
			BitmapDrawable drawableRT = new BitmapDrawable(mEditRotateRT);
			drawableRT.setBounds(0, 0, drawableRT.getBitmap().getWidth(), drawableRT.getBitmap().getHeight());
			canvas.setMatrix(rightTopMatrix);
			drawableRT.draw(canvas);
			Matrix leftTopMatrix = new Matrix(this.mEditButtonMatrix);
			leftTopMatrix.postTranslate(mLeftTop.x, mLeftTop.y);
			BitmapDrawable drawableLT = new BitmapDrawable(mEditSizeLT);
			drawableLT.setBounds(0, 0, drawableLT.getBitmap().getWidth(), drawableLT.getBitmap().getHeight());
			canvas.setMatrix(leftTopMatrix);
			drawableLT.draw(canvas);
			Matrix rightBottomMatrix = new Matrix(this.mEditButtonMatrix);
			rightBottomMatrix.postTranslate(mRightBottom.x, mRightBottom.y);
			BitmapDrawable drawableRB = new BitmapDrawable(mEditSizeRB);
			drawableRB.setBounds(0, 0, drawableRB.getBitmap().getWidth(), drawableRB.getBitmap().getHeight());
			canvas.setMatrix(rightBottomMatrix);
			drawableRB.draw(canvas);
			Matrix leftBottomMatrix = new Matrix(this.mEditButtonMatrix);
			leftBottomMatrix.postTranslate(mLeftBottom.x, mLeftBottom.y);
			BitmapDrawable drawableLB = new BitmapDrawable(mEditSizeLB);
			drawableLB.setBounds(0, 0, drawableLB.getBitmap().getWidth(), drawableLB.getBitmap().getHeight());
			canvas.setMatrix(leftBottomMatrix);
			drawableLB.draw(canvas);
			canvas.setMatrix(new Matrix());
		}
	}

	/**
	 * アイテム情報を更新する
	 */
	private void updateItem() {
		mLeft = (center.x - width / 2);
		mRight = (center.x + width / 2);
		mTop = (center.y - height / 2);
		mBottom = (center.y + height / 2);
		float[] arrayOfFloat1 = new float[8];
		arrayOfFloat1[0] = (mLeft - center.x);
		arrayOfFloat1[1] = (mTop - center.y);
		arrayOfFloat1[2] = (mRight - center.x);
		arrayOfFloat1[3] = (mTop - center.y);
		arrayOfFloat1[4] = (mRight - center.x);
		arrayOfFloat1[5] = (mBottom - center.y);
		arrayOfFloat1[6] = (mLeft - center.x);
		arrayOfFloat1[7] = (mBottom - center.y);
		float[] arrayOfFloat2 = new float[8];
		Matrix calcMatrix = new Matrix(mBitmapMatrix);
		if(mDirection == 1) {
			calcMatrix.preScale(-1.0f, 1.0f);
		}
		calcMatrix.mapPoints(arrayOfFloat2, arrayOfFloat1);
		mLeftTop = new PointF(arrayOfFloat2[0] + center.x, arrayOfFloat2[1] + center.y);
		mRightTop = new PointF(arrayOfFloat2[2] + center.x, arrayOfFloat2[3] + center.y);
		mRightBottom = new PointF(arrayOfFloat2[4] + center.x, arrayOfFloat2[5] + center.y);
		mLeftBottom = new PointF(arrayOfFloat2[6] + center.x, arrayOfFloat2[7] + center.y);
	}

	/**
	 * ターゲットのビットマップを読み込む
	 */
	private void loadTargetBitmap() {
		mTargetBitmap = null;
		try {
			Bitmap bitmap = Bitmap.createScaledBitmap(mBitmap, (int)width, (int)height, true);
			mTargetBitmap = Bitmap.createBitmap(bitmap, 0, 0, (int)width, (int)height, mBitmapMatrix, true);
			if(bitmap.hashCode() != mTargetBitmap.hashCode() && bitmap.hashCode() != mBitmap.hashCode()) {
				bitmap.recycle();
				bitmap = null;
			}
			return;
		} catch (OutOfMemoryError localOutOfMemoryError) {
			Log.e("Error", localOutOfMemoryError.toString());
		}
	}

	/**
	 * 幅と高さを設定する
	 * @param newWidth
	 */
	public void setWidth(float newWidth) {
		height = newWidth * mRatio;
		width = newWidth;
		updateItem();
	}

	/**
	 * 中心点を設定する
	 * @param newCenterPoint
	 */
	public void setCenter(PointF newCenterPoint) {
		center = newCenterPoint;
		updateItem();
	}

	/**
	 * 移動する
	 * @param toX
	 * @param toY
	 */
	public void translate(float toX, float toY) {
		PointF newCenterPoint = new PointF(toX + center.x, toY + center.y);
		setCenter(newCenterPoint);
	}

	/**
	 * 2点間の距離を計算する
	 * @param point1
	 * @param point2
	 * @return
	 */
	private double calcDistance(PointF point1, PointF point2) {
		// X軸の差
		float diffX = point1.x - point2.x;
		// Y軸の差
		float diffY = point1.y - point2.y;
		// 3平方の定理よりpoint1とpoint2の距離を求める
		double distance = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
		return distance;
	}

	/**
	 * 当たり判定をする
	 * @param touchPoint
	 * @return
	 */
	public int collisionDetection(PointF touchPoint) {
		updateItem();

		if(calcDistance(touchPoint, mRightTop) <= 30.0F) {
			// Rotate
			return EditType.EDIT_TYPE_ROTATE;
		}
		if(calcDistance(touchPoint, mLeftTop) <= 30.0F ||
		   calcDistance(touchPoint, mRightBottom) <= 30.0F ||
		   calcDistance(touchPoint, mLeftBottom) <= 30.0F) {
			// Resize
			return EditType.EDIT_TYPE_RESIZE;
		}

		Path path = new Path();
		path.moveTo(mLeftTop.x, mLeftTop.y);
		path.lineTo(mRightTop.x, mRightTop.y);
		path.lineTo(mRightBottom.x, mRightBottom.y);
		path.lineTo(mLeftBottom.x, mLeftBottom.y);
		path.lineTo(mLeftTop.x, mLeftTop.y);
		path.close();
		Region clipArea = new Region((int)(mLeft - width), (int)(mTop - height), (int)(mRight + width), (int)(mBottom + height));
	    Region targetArea = new Region();
	    targetArea.setPath(path, clipArea);
	    if (targetArea.contains((int)touchPoint.x, (int)touchPoint.y)) {
	    	return EditType.EDIT_TYPE_TRANSLATE;
	    }
	    return EditType.EDIT_TYPE_NONE;
	}

	/**
	 * 回転させる
	 * @param rotateParam
	 */
	public void rotate(float rotateParam) {
		mBitmapMatrix.postRotate(rotateParam);
		mEditButtonMatrix.postRotate(rotateParam);
	}

	/**
	 * 反転させる
	 */
	public void reverse() {
		mBitmapMatrix.preScale(-1.0f, 1.0f);
		if(mDirection == 0) {
			mDirection = 1;
		} else {
			mDirection = 0;
		}
	}
}
