package org.rainboweleven.rbridge.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;

import org.rainboweleven.rbridge.core.RWebViewInterface;

/**
 * SystemWebView WebChromeClient
 *
 * @author andy(Andy)
 * @datetime 2018-04-25 11:46 GMT+8
 * @email 411086563@qq.com
 */
public class SystemWebChromeClient extends WebChromeClient {

    private RWebViewInterface mRWebViewInterface;
    private WebChromeClient mWebChromeClient;

    public SystemWebChromeClient(RWebViewInterface webViewInterface) {
        super();
        this.mRWebViewInterface = webViewInterface;
    }

    public void setWebChromeClient(WebChromeClient webChromeClient) {
        mWebChromeClient = webChromeClient;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onProgressChanged(view, newProgress);
        } else {
            super.onProgressChanged(view, newProgress);
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        final Context context = view.getContext();
        // FIXME 这里待优化
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                // WebView已准备好了
                // 插件管理器可以干活了
                RBridgePluginManager.getInstance().onRWebViewReady(mRWebViewInterface);
                // 系统事件接收器可以干活了
                REventsCenter.getInstance(context).onRWebViewReady(mRWebViewInterface);
            }
        }, 1000);
        if (mWebChromeClient != null) {
            mWebChromeClient.onReceivedTitle(view, title);
        } else {
            super.onReceivedTitle(view, title);
        }
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onReceivedIcon(view, icon);
        } else {
            super.onReceivedIcon(view, icon);
        }
    }

    @Override
    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onReceivedTouchIconUrl(view, url, precomposed);
        } else {
            super.onReceivedTouchIconUrl(view, url, precomposed);
        }
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onShowCustomView(view, callback);
        } else {
            super.onShowCustomView(view, callback);
        }
    }

    @Override
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onShowCustomView(view, requestedOrientation, callback);
        } else {
            super.onShowCustomView(view, requestedOrientation, callback);
        }
    }

    @Override
    public void onHideCustomView() {
        if (mWebChromeClient != null) {
            mWebChromeClient.onHideCustomView();
        } else {
            super.onHideCustomView();
        }
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        if (mWebChromeClient != null) {
            return mWebChromeClient.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        } else {
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }
    }

    @Override
    public void onRequestFocus(WebView view) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onRequestFocus(view);
        } else {
            super.onRequestFocus(view);
        }
    }

    @Override
    public void onCloseWindow(WebView window) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onCloseWindow(window);
        } else {
            super.onCloseWindow(window);
        }
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        if (mWebChromeClient != null) {
            return mWebChromeClient.onJsAlert(view, url, message, result);
        } else {
            return super.onJsAlert(view, url, message, result);
        }
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        if (mWebChromeClient != null) {
            return mWebChromeClient.onJsConfirm(view, url, message, result);
        } else {
            return super.onJsConfirm(view, url, message, result);
        }
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        if (mWebChromeClient != null) {
            return mWebChromeClient.onJsPrompt(view, url, message, defaultValue, result);
        } else {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
        if (mWebChromeClient != null) {
            return mWebChromeClient.onJsBeforeUnload(view, url, message, result);
        } else {
            return super.onJsBeforeUnload(view, url, message, result);
        }
    }

    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long
            estimatedDatabaseSize, long totalQuota, QuotaUpdater quotaUpdater) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize,
                    totalQuota, quotaUpdater);
        } else {
            super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota,
                    quotaUpdater);
        }
    }

    @Override
    public void onReachedMaxAppCacheSize(long requiredStorage, long quota, QuotaUpdater quotaUpdater) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
        } else {
            super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
        }
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onGeolocationPermissionsShowPrompt(origin, callback);
        } else {
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        if (mWebChromeClient != null) {
            mWebChromeClient.onGeolocationPermissionsHidePrompt();
        } else {
            super.onGeolocationPermissionsHidePrompt();
        }
    }

    @Override
    public void onPermissionRequest(PermissionRequest request) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onPermissionRequest(request);
        } else {
            super.onPermissionRequest(request);
        }
    }

    @Override
    public void onPermissionRequestCanceled(PermissionRequest request) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onPermissionRequestCanceled(request);
        } else {
            super.onPermissionRequestCanceled(request);
        }
    }

    @Override
    public boolean onJsTimeout() {
        if (mWebChromeClient != null) {
            return mWebChromeClient.onJsTimeout();
        } else {
            return super.onJsTimeout();
        }
    }

    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onConsoleMessage(message, lineNumber, sourceID);
        } else {
            super.onConsoleMessage(message, lineNumber, sourceID);
        }
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        if (mWebChromeClient != null) {
            return mWebChromeClient.onConsoleMessage(consoleMessage);
        } else {
            return super.onConsoleMessage(consoleMessage);
        }
    }

    @Override
    public Bitmap getDefaultVideoPoster() {
        if (mWebChromeClient != null) {
            return mWebChromeClient.getDefaultVideoPoster();
        } else {
            return super.getDefaultVideoPoster();
        }
    }

    @Override
    public View getVideoLoadingProgressView() {
        if (mWebChromeClient != null) {
            return mWebChromeClient.getVideoLoadingProgressView();
        } else {
            return super.getVideoLoadingProgressView();
        }
    }

    @Override
    public void getVisitedHistory(ValueCallback<String[]> callback) {
        if (mWebChromeClient != null) {
            mWebChromeClient.getVisitedHistory(callback);
        } else {
            super.getVisitedHistory(callback);
        }
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams
            fileChooserParams) {
        if (mWebChromeClient != null) {
            return mWebChromeClient.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        } else {
            return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        }
    }
}
