package org.rainboweleven.rbridge.impl;

import android.content.Intent;
import android.net.Uri;
import android.webkit.DownloadListener;

import org.rainboweleven.rbridge.core.RWebViewInterface;

/**
 * SystemWebView DownloadListener
 *
 * @author andy(Andy)
 * @datetime 2018-04-25 14:19 GMT+8
 * @email 411086563@qq.com
 */
public class SystemDownloadListener implements DownloadListener {

    private RWebViewInterface mRWebViewInterface;
    private DownloadListener mDownloadListener;

    public SystemDownloadListener(RWebViewInterface webViewInterface) {
        this.mRWebViewInterface = webViewInterface;
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        mDownloadListener = downloadListener;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long
            contentLength) {
        if (mDownloadListener != null) {
            mDownloadListener.onDownloadStart(url, userAgent, contentDisposition, mimetype, contentLength);
        } else {
            // 默认实现
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            mRWebViewInterface.context().startActivity(intent);
        }
    }
}
