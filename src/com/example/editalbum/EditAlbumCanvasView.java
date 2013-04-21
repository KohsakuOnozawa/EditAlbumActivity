package com.example.editalbum;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Environment;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * キャンバスViewクラス
 * @author kohsaku
 *
 */
public class EditAlbumCanvasView extends SurfaceView implements SurfaceHolder.Callback {

	/**
	 * アクティビティ
	 */
	public EditAlbumActivity activity;

	/**
	 * 配置アイテムリス図
	 */
	public LinkedList<PlacedItem> placeditems = new LinkedList<PlacedItem>();

	/**
	 * スケールジェスチャーディレクタ
	 */
	private ScaleGestureDetector mScaleGestureDetector;

	/**
	 * ジェスチャーディレクタ
	 */
	private GestureDetector mGestureDetector;

	/**
	 * 前回のスケール
	 */
	private float mPreScale;

	/**
	 * ターゲットアイテム
	 */
	private PlacedItem mTargetItem;

	/**
	 * ターゲットアイテムのレイヤの位置
	 */
	private int mTargetItemIndex = -1;

	/**
	 * 前回のタッチポイント
	 */
	private PointF mPreTouchPoint = new PointF(0.0F, 0.0F);

	/**
	 * タッチポイント
	 */
	private PointF mTouchPoint = new PointF(0.0F, 0.0F);

	/**
	 * 編集タイプ
	 */
	private int mEditType;

	/**
	 * 前回のX座標
	 */
	private float mPreX;

	/**
	 * 前回のY座標
	 */
	private float mPreY;

	/**
	 * コンストラクタ
	 * @param context
	 */
	public EditAlbumCanvasView(Context context) {
		super(context);
		getHolder().addCallback(this);
		setLongClickable(true);
		mScaleGestureDetector = new ScaleGestureDetector(context, mOnScaleGestureListener);
		mGestureDetector = new GestureDetector(context, mOnGestureListener);
	}

	/**
	 * ジェスチャーリスナー
	 */
	private final GestureDetector.SimpleOnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

		/* (非 Javadoc)
		 * @see android.view.GestureDetector.SimpleOnGestureListener#onDown(android.view.MotionEvent)
		 */
		@Override
		public boolean onDown(MotionEvent e) {
			// イベントのX,Yを取得
			float eventX = e.getX();
			float eventY = e.getY();

			// 前回のタッチポイントを設定
			mPreTouchPoint = mTouchPoint;

			// 今回のタッチポイントを設定
			PointF eventPoint = new PointF(eventX, eventY);
			mTouchPoint = eventPoint;

			mPreX = eventX;
			mPreY = eventY;

			// 選択済みのアイテムがない場合
			if(mTargetItem == null) {
				// EditTypeにターゲットなしを設定
				mEditType = EditType.EDIT_TYPE_NO_TARGET_ITEM;

				// アイテムリストの上のレイヤのものから調べる
				PlacedItem placedItem = null;
				int editType;
				for(int forIndex = placeditems.size() - 1; forIndex >= 0; forIndex--) {
					placedItem = placeditems.get(forIndex);
					// 当たり判定をする
					editType = placedItem.collisionDetection(mTouchPoint);
					// EditTypeがTranslateならターゲットアイテムにする
					if(editType == EditType.EDIT_TYPE_TRANSLATE) {
						setSelectItem(placedItem, forIndex);
						break;
					}
				}
				return false;
			// 選択済みのアイテムがある場合
			} else {
				// 当たり判定をする
				int editType = mTargetItem.collisionDetection(mTouchPoint);
				if(editType == EditType.EDIT_TYPE_TRANSLATE || editType == EditType.EDIT_TYPE_RESIZE || editType == EditType.EDIT_TYPE_ROTATE) {
					mEditType = editType;
					return true;
				} else {
					return true;
				}
			}
		}

		/* (非 Javadoc)
		 * @see android.view.GestureDetector.SimpleOnGestureListener#onFling(android.view.MotionEvent, android.view.MotionEvent, float, float)
		 */
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return false;
		}

		/* (非 Javadoc)
		 * @see android.view.GestureDetector.SimpleOnGestureListener#onLongPress(android.view.MotionEvent)
		 */
		@Override
		public void onLongPress(MotionEvent e) {
			super.onLongPress(e);
		}

		/* (非 Javadoc)
		 * @see android.view.GestureDetector.SimpleOnGestureListener#onScroll(android.view.MotionEvent, android.view.MotionEvent, float, float)
		 */
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			// イベントのX,Yを取得
			float eventX = e2.getX();
			float eventY = e2.getY();

			// 前回のタッチポイントを設定
			mPreTouchPoint = mTouchPoint;

			// 今回のタッチポイントを設定
			PointF eventPoint = new PointF(eventX, eventY);
			mTouchPoint = eventPoint;

			// 編集タイプによって分岐
			switch (mEditType) {
				// 移動の場合
				case EditType.EDIT_TYPE_TRANSLATE:
					mTargetItem.translate(mTouchPoint.x - mPreTouchPoint.x, mTouchPoint.y - mPreTouchPoint.y);
					updateLayouts();
					break;
				// 回転の場合
				case EditType.EDIT_TYPE_ROTATE:
					float degrees = (float)(Math.toDegrees(Math.atan2(eventY - mTargetItem.center.y, eventX - mTargetItem.center.x)) -
							Math.toDegrees(Math.atan2(mPreY - mTargetItem.center.y, mPreX- mTargetItem.center.x)));
					mTargetItem.rotate(degrees);
					updateLayouts();
					mPreX = eventX;
					mPreY = eventY;
					break;
				// リサイズの場合
				case EditType.EDIT_TYPE_RESIZE:
					float newWidth = (float)((calcDistance(mTargetItem.center, mTouchPoint) - calcDistance(mTargetItem.center, mPreTouchPoint)) * Math.sqrt(2.0d)) + mTargetItem.width;
					// 最小サイズ以上の場合
					if(newWidth >= 50.0f) {
						mTargetItem.setWidth(newWidth);
						updateLayouts();
					}
					break;
				default:
					break;
			}
			return false;
		}

		/* (非 Javadoc)
		 * @see android.view.GestureDetector.SimpleOnGestureListener#onShowPress(android.view.MotionEvent)
		 */
		@Override
		public void onShowPress(MotionEvent e) {
			super.onShowPress(e);
		}

		/* (非 Javadoc)
		 * @see android.view.GestureDetector.SimpleOnGestureListener#onSingleTapUp(android.view.MotionEvent)
		 */
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// 選択済みのアイテムがある場合
			if(mTargetItem != null) {
				// 当たり判定をする
				int editType = mTargetItem.collisionDetection(mTouchPoint);
				//　前回も今回もTranslateの場合
				if((editType == EditType.EDIT_TYPE_TRANSLATE && mEditType == EditType.EDIT_TYPE_TRANSLATE) || editType == EditType.EDIT_TYPE_NONE) {
					// 選択済みのアイテムより下のレイヤを調べる
					PlacedItem placedItem = null;
					int placedItemEditType;
					for(int forIndex = mTargetItemIndex - 1; forIndex >= 0; forIndex--) {
						if(forIndex < 0) {
							break;
						}
						placedItem = placeditems.get(forIndex);
						// 当たり判定をする
						placedItemEditType = placedItem.collisionDetection(mTouchPoint);
						// EditTypeがTranslateならターゲットアイテムにする
						if(placedItemEditType == EditType.EDIT_TYPE_TRANSLATE) {
							// 選択済みのアイテムをはずす
							setNotSelectedItem();
							setSelectItem(placedItem, forIndex);
							mEditType = placedItemEditType;
							return false;
						}
					}
					// 選択済みのアイテムをはずす
					setNotSelectedItem();
				}
			}
			return false;
		}

	};


	/**
	 * スケールジェスチャーリスナー
	 */
	private final ScaleGestureDetector.SimpleOnScaleGestureListener mOnScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {

		/* (非 Javadoc)
		 * @see android.view.ScaleGestureDetector.SimpleOnScaleGestureListener#onScale(android.view.ScaleGestureDetector)
		 */
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			// 今回のスケール係数と前回との割合を取得する
			float ratio = detector.getScaleFactor() / mPreScale;
			// 前回のスケール係数を今回ので上書きする
			mPreScale = detector.getScaleFactor();
			if(mTargetItem != null) {
				float minSize = 30.0f * activity.getResources().getDisplayMetrics().density;
				float tWidth = ratio * mTargetItem.width;
				float tHeight =ratio * mTargetItem.height;
				if(tWidth >= minSize || tHeight >= minSize) {
					PointF centerPoint = new PointF(mTargetItem.center.x, mTargetItem.center.y);
					mTargetItem.setWidth(tWidth);
					mTargetItem.setCenter(centerPoint);
					updateLayouts();

				}
				return false;
			}
			return false;
		}

		/* (非 Javadoc)
		 * @see android.view.ScaleGestureDetector.SimpleOnScaleGestureListener#onScaleBegin(android.view.ScaleGestureDetector)
		 */
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			// スケール係数の初期値を設定
			mPreScale = 1.0f;
			return super.onScaleBegin(detector);
		}

		/* (非 Javadoc)
		 * @see android.view.ScaleGestureDetector.SimpleOnScaleGestureListener#onScaleEnd(android.view.ScaleGestureDetector)
		 */
		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			super.onScaleEnd(detector);
		}
	};

	/* (非 Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder, int, int, int)
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		doDraw(getHolder());
	}

	/* (非 Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		doDraw(getHolder());
	}

	/* (非 Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	/**
	 * 描写処理
	 * @param surfaceHolder
	 */
	private void doDraw(SurfaceHolder surfaceHolder) {
		Canvas canvas = surfaceHolder.lockCanvas();
		if(canvas == null) {
			return;
		}
		canvas.drawColor(Color.WHITE);
		Iterator<PlacedItem> placedItemsIterator = placeditems.iterator();
		while (true) {
			if(!placedItemsIterator.hasNext()) {
				surfaceHolder.unlockCanvasAndPost(canvas);
				return;
			}
			placedItemsIterator.next().drawInCanvas(canvas);
		}
	}

	/* (非 Javadoc)
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return event.getPointerCount() == 1 ? mGestureDetector.onTouchEvent(event) : mScaleGestureDetector.onTouchEvent(event);

	}

	/**
	 * レイアウトを更新する
	 */
	public void updateLayouts() {
		doDraw(getHolder());
	}

	/**
	 * アイテムを追加する
	 * @param placedItem
	 */
	public void addItem(PlacedItem placedItem) {
		setTargetItem(placedItem);
		placeditems.add(placedItem);
		mTargetItemIndex = placeditems.size() - 1;
		updateLayouts();
	}

	/**
	 * ターゲットアイテムに設定する
	 * @param placedItem
	 */
	public void setTargetItem(PlacedItem placedItem) {
		if(mTargetItem != null) {
			setNotSelectedItem();
		}
		mTargetItem = placedItem;
		placedItem.isSelected = true;
		mEditType = EditType.EDIT_TYPE_TRANSLATE;
	}

	/**
	 * アイテムを選択する
	 * @param placedItem
	 * @param index
	 */
	private void setSelectItem(PlacedItem placedItem, int index) {
		mTargetItem = placedItem;
		mTargetItem.isSelected = true;
		mTargetItemIndex = index;
		updateLayouts();
	}

	/**
	 * アイテムを未選択にする
	 */
	private void setNotSelectedItem() {
		mTargetItem.isSelected = false;
		mTargetItem = null;
		mTargetItemIndex = -1;
		mEditType = EditType.EDIT_TYPE_NO_TARGET_ITEM;
		updateLayouts();
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
	 * 1つ上のレイヤに移動させる
	 */
	public void uplayer() {
		// 選択されていない、または一番上のレイヤの場合は、何もしない
		if(mTargetItem == null || mTargetItemIndex == placeditems.size() - 1) {
			return;
		}
		PlacedItem placedItem = placeditems.remove(mTargetItemIndex);
		mTargetItemIndex += 1;
		placeditems.add(mTargetItemIndex, placedItem);
		updateLayouts();
	}

	/**
	 * 1つ下のレイヤに移動させる
	 */
	public void downlayer() {
		// 選択されていない、または一番下のレイヤの場合は、何もしない
		if(mTargetItem == null || mTargetItemIndex == 0) {
			return;
		}
		PlacedItem placedItem = placeditems.remove(mTargetItemIndex);
		mTargetItemIndex -= 1;
		placeditems.add(mTargetItemIndex, placedItem);
		updateLayouts();
	}

	/**
	 * 反転させる
	 */
	public void reverse() {
		// 選択されていない場合は、何もしない
		if(mTargetItem == null) {
			return;
		}
		mTargetItem.reverse();
		updateLayouts();
	}

	/**
	 * 削除する
	 */
	public void discard() {
		// 選択されていない場合は、何もしない
		if(mTargetItem == null) {
			return;
		}
		placeditems.remove(mTargetItemIndex);
		setNotSelectedItem();
	}

	/**
	 * 画像を作成する
	 * @param fileName
	 */
	public void createAlbum(String fileName) {
		Bitmap baseBitmap = Bitmap.createBitmap(600, 600, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(baseBitmap);
		canvas.drawColor(Color.WHITE);
		Iterator<PlacedItem> placedItemsIterator = placeditems.iterator();
		PlacedItem placedItem;
		while (true) {
			if(!placedItemsIterator.hasNext()) {
				break;
			}
			placedItem = placedItemsIterator.next();
			placedItem.isForceLoadBitmap = true;
			placedItem.isSelected = false;
			placedItem.drawInCanvas(canvas);
		}

		try {
			OutputStream outputStream = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/Pictures/Decollage/IMG_" + fileName + ".jpg");
			try {
				baseBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
			} catch (Exception e) {
				e.printStackTrace();
			}
			outputStream.close();
			baseBitmap.recycle();
			baseBitmap = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// ギャラリーにアルバムを表示させる
		new MediaScannerNotifier(activity, Environment.getExternalStorageDirectory().getPath() + "/Pictures/Decollage/IMG_" + fileName + ".jpg", "image/jpeg");
	}
}
