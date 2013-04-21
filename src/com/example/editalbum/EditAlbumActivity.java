package com.example.editalbum;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * アルバム編集アクティビティクラス
 * @author kohsaku
 *
 */
public class EditAlbumActivity extends Activity {


	/**
	 * ファイル名に使う日付フォーマット
	 */
	public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";

	/**
	 * キャンバスView
	 */
	private EditAlbumCanvasView mCanvasView;

	/**
	 * キャンバスLayout
	 */
	private FrameLayout mCanvasLayout;

	/* (非 Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// キャンバスLayoutにキャンバスViewを追加する
		mCanvasLayout = (FrameLayout)findViewById(R.id.canvasLayout);
		mCanvasView = new EditAlbumCanvasView(this);
		mCanvasView.activity = this;
		mCanvasLayout.addView(mCanvasView, new ViewGroup.LayoutParams(-1, -1));

		// ItemAボタンが押されたときの処理
		Button itemABtn = (Button)findViewById(R.id.itemABtn);
		itemABtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// ItemA 画像を読み込み、キャンバスViewに追加する
				Bitmap targetBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.itema);
				PlacedItem placedItem = new PlacedItem(EditAlbumActivity.this, targetBitmap);
				placedItem.setWidth(mCanvasLayout.getWidth() / 2);
				placedItem.setCenter(new PointF(mCanvasLayout.getWidth() / 2 , mCanvasLayout.getHeight() / 2));
				mCanvasView.addItem(placedItem);
			}
		});

		// ItemBボタンが押されたときの処理
		Button itemBBtn = (Button)findViewById(R.id.itemBBtn);
		itemBBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// ItemB 画像を読み込み、キャンバスViewに追加する
				Bitmap targetBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.itemb);
				PlacedItem placedItem = new PlacedItem(EditAlbumActivity.this, targetBitmap);
				placedItem.setWidth(mCanvasLayout.getWidth() / 2);
				placedItem.setCenter(new PointF(mCanvasLayout.getWidth() / 2 , mCanvasLayout.getHeight() / 2));
				mCanvasView.addItem(placedItem);
			}
		});

		// ItemCボタンが押されたときの処理
		Button itemCBtn = (Button)findViewById(R.id.itemCBtn);
		itemCBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// ItemC 画像を読み込み、キャンバスViewに追加する
				Bitmap targetBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.itemc);
				PlacedItem placedItem = new PlacedItem(EditAlbumActivity.this, targetBitmap);
				placedItem.setWidth(mCanvasLayout.getWidth() / 2);
				placedItem.setCenter(new PointF(mCanvasLayout.getWidth() / 2 , mCanvasLayout.getHeight() / 2));
				mCanvasView.addItem(placedItem);
			}
		});

		// 1レイヤ上へボタンが押されたときの処理
		Button upLayerBtn = (Button)findViewById(R.id.upLayerBtn);
		upLayerBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 選択されているアイテムのレイヤを1つ上に移動
				mCanvasView.uplayer();
			}
		});

		// 1レイヤ下へボタンが押されたときの処理
		Button downLayerBtn = (Button)findViewById(R.id.downLayerBtn);
		downLayerBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 選択されているアイテムのレイヤを1つ下に移動
				mCanvasView.downlayer();
			}
		});

		// 反転ボタンが押されたときの処理
		Button reverseBtn = (Button)findViewById(R.id.reverseBtn);
		reverseBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 選択されているアイテムを反転させる
				mCanvasView.reverse();
			}
		});

		// 捨てるボタンが押されたときの処理
		Button discardBtn = (Button)findViewById(R.id.discardBtn);
		discardBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 選択されているアイテムをキャンバスViewから削除する
				mCanvasView.discard();
			}
		});

		// 横文字ボタンが押されたときの処理
		Button charBtn = (Button)findViewById(R.id.charBtn);
		charBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 横方向に、「あいうえお」という文字の 画像を作成し、キャンバスViewに追加する
				String str = "あいうえお";
				Paint paint = new Paint();
				paint.setAntiAlias(true);
				paint.setTypeface(Typeface.DEFAULT);
				paint.setColor(Color.BLUE);
				paint.setTextSize(70);

				Rect rect = new Rect();

				paint.getTextBounds(str, 0, str.length(), rect);
				FontMetrics fm = paint.getFontMetrics();

				int width = (int)paint.measureText(str);
				int height = (int)paint.measureText(str);
				Bitmap bitmap = Bitmap.createBitmap(width + 1 * 2, height + 1 * 2, Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmap);
				canvas.drawText(str, 1, (int)paint.measureText(str) / 2 + (int) (Math.abs(fm.top) + fm.bottom) / 4, paint);

				PlacedItem placedItem = new PlacedItem(EditAlbumActivity.this, bitmap);
				placedItem.setCenter(new PointF(mCanvasLayout.getWidth() / 2 , mCanvasLayout.getHeight() / 2));
				mCanvasView.addItem(placedItem);
			}
		});

		// 縦文字ボタンが押されたときの処理
		Button char2Btn = (Button)findViewById(R.id.char2Btn);
		char2Btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 縦方向に、「あいうえお」という文字の 画像を作成し、キャンバスViewに追加する
				String str = "あいうえお";
				Paint paint = new Paint();
				paint.setAntiAlias(true);
				paint.setTypeface(Typeface.DEFAULT);
				paint.setColor(Color.BLUE);
				paint.setTextSize(70);

				Rect rect = new Rect();

				paint.getTextBounds(str, 0, str.length(), rect);
				FontMetrics fm = paint.getFontMetrics();

				int width = (int)paint.measureText(str);
				int height = (int)paint.measureText(str);
				Bitmap bitmap = Bitmap.createBitmap(width + 1 * 2, height + 1 * 2, Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmap);
				int strLength = str.length();
				int low = (int)(Math.abs(fm.top) + fm.bottom) * 3 / 4;
				int h = 0;
				for(int i = 0; i < strLength; i++) {
					String strC = String.valueOf(str.charAt(i));
					h += low;
					canvas.drawText(strC, (int)paint.measureText(str) / 2 - (int) (Math.abs(fm.top) + fm.bottom) / 4, h, paint);
				}

				PlacedItem placedItem = new PlacedItem(EditAlbumActivity.this, bitmap);
				placedItem.setCenter(new PointF(mCanvasLayout.getWidth() / 2 , mCanvasLayout.getHeight() / 2));
				mCanvasView.addItem(placedItem);
			}
		});

		// 画像作成ボタンが押されたときの処理
		Button createImageBtn = (Button)findViewById(R.id.createImageBtn);
		createImageBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// キャンバスViewに表示されているものを、jpg画像として保存する
				String fileName = new SimpleDateFormat(DATE_FORMAT, Locale.JAPAN).format(Calendar.getInstance().getTime());
				mCanvasView.createAlbum(fileName);
			}
		});
	}
}
