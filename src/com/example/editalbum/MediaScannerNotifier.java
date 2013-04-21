package com.example.editalbum;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

/**
 * 作成した画像をギャラリーに表示させるためのクラス
 * @author kohsaku
 *
 */
public class MediaScannerNotifier implements MediaScannerConnectionClient {
	private Context mContext;
	private MediaScannerConnection mConnection;
	private String mPath;
	private String mMimeType;

	/**
	 * コンストラクタ
	 * @param context
	 * @param path
	 * @param mimeType
	 */
	public MediaScannerNotifier(Context context, String path, String mimeType) {
		mContext = context;
		mPath = path;
		mMimeType = mimeType;
		mConnection = new MediaScannerConnection(mContext, this);
		mConnection.connect();
	}

	/* (非 Javadoc)
	 * @see android.media.MediaScannerConnection.MediaScannerConnectionClient#onMediaScannerConnected()
	 */
	@Override
	public void onMediaScannerConnected() {
		mConnection.scanFile(mPath, mMimeType);
	}

	/* (非 Javadoc)
	 * @see android.media.MediaScannerConnection.MediaScannerConnectionClient#onScanCompleted(java.lang.String, android.net.Uri)
	 */
	@Override
	public void onScanCompleted(String path, Uri uri) {
	}
}
