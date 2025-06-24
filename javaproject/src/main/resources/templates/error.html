package com.teama.javaproject.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/**
 * エラーハンドリングコントローラー
 */
@Controller
@ControllerAdvice
public class CustomErrorController implements ErrorController {

    /**
     * エラーページの表示
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        
        String errorCode = "500";
        String errorTitle = "システムエラー";
        String errorDescription = "申し訳ございませんが、システムでエラーが発生しました。";
        String suggestion = "しばらく時間をおいてから再度お試しください。";
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            errorCode = String.valueOf(statusCode);
            
            switch (statusCode) {
                case 400:
                    errorTitle = "不正なリクエスト";
                    errorDescription = "リクエストに問題があります。";
                    suggestion = "入力内容を確認してから再度お試しください。";
                    break;
                case 403:
                    errorTitle = "アクセス権限エラー";
                    errorDescription = "このページにアクセスする権限がありません。";
                    suggestion = "ログイン状況を確認するか、管理者にお問い合わせください。";
                    break;
                case 404:
                    errorTitle = "ページが見つかりません";
                    errorDescription = "お探しのページは存在しないか、移動された可能性があります。";
                    suggestion = "URLを確認するか、ホームページから目的のページを探してください。";
                    break;
                case 500:
                    errorTitle = "サーバーエラー";
                    errorDescription = "サーバーで予期しないエラーが発生しました。";
                    suggestion = "しばらく時間をおいてから再度お試しください。問題が続く場合は管理者にお問い合わせください。";
                    break;
                case 503:
                    errorTitle = "サービス利用不可";
                    errorDescription = "現在サービスが利用できません。";
                    suggestion = "メンテナンス中の可能性があります。しばらく時間をおいてから再度お試しください。";
                    break;
            }
        }
        
        model.addAttribute("errorCode", errorCode);
        model.addAttribute("errorTitle", errorTitle);
        model.addAttribute("errorDescription", errorDescription);
        model.addAttribute("suggestion", suggestion);
        model.addAttribute("requestUri", requestUri);
        
        // 詳細情報（開発時のみ表示）
        if (exception != null) {
            model.addAttribute("exceptionMessage", exception.toString());
        }
        if (message != null) {
            model.addAttribute("errorMessage", message.toString());
        }
        
        return "error";
    }
    
    /**
     * 一般的な例外ハンドリング
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception e, Model model) {
        System.err.println("予期しないエラーが発生しました: " + e.getMessage());
        e.printStackTrace();
        
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorTitle", "システムエラー");
        model.addAttribute("errorDescription", "予期しないエラーが発生しました。");
        model.addAttribute("suggestion", "管理者にお問い合わせください。");
        model.addAttribute("exceptionMessage", e.getMessage());
        
        return "error";
    }
    
    /**
     * IllegalArgumentException ハンドリング
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException e, Model model) {
        System.err.println("不正な引数エラー: " + e.getMessage());
        
        model.addAttribute("errorCode", "400");
        model.addAttribute("errorTitle", "入力エラー");
        model.addAttribute("errorDescription", "入力データに問題があります。");
        model.addAttribute("suggestion", "入力内容を確認してから再度お試しください。");
        model.addAttribute("exceptionMessage", e.getMessage());
        
        return "error";
    }
    
    /**
     * RuntimeException ハンドリング
     */
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException e, Model model) {
        System.err.println("実行時エラー: " + e.getMessage());
        e.printStackTrace();
        
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorTitle", "処理エラー");
        model.addAttribute("errorDescription", "処理中にエラーが発生しました。");
        model.addAttribute("suggestion", "しばらく時間をおいてから再度お試しください。");
        model.addAttribute("exceptionMessage", e.getMessage());
        
        return "error";
    }
}