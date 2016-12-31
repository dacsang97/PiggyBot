package com.sangnd.piggyboom.controllers;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sangnd.piggyboom.model.LoginData;
import com.sangnd.piggyboom.model.PlayData;
import com.sangnd.piggyboom.model.URL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Controller extends Utils{

    @FXML
    private VBox loginform;

    @FXML
    private TextField token;

    @FXML
    private HBox userinfo;

    @FXML
    private Label name;

    @FXML
    private ImageView avatar;

    @FXML
    private TextArea message;

    private JSONObject mResponse;
    private PlayData mBaseData;
    private String accessToken;
    private boolean isLogin = false;
    private String status;

    private void loggedIn() {
        loginform.getStyleClass().add("logged");
        userinfo.getStyleClass().remove("logged");
        name.setText(mResponse.getString("name"));
        avatar.setImage(new Image(mResponse.getString("fbpic")));
        message.setText(String.format("Login as: %s \n", mResponse.getString("name")));
    }

    private void loginFailure() {
        token.setText("");
        token.setPromptText("Xảy ra lỗi khi đăng nhập.");
    }

    private void updateMessage(String newMessage) {
        String currentMessage = message.getText();
        currentMessage = String.format("%s%s\n", currentMessage, newMessage);
        message.setText(currentMessage);
        message.setScrollTop(Double.MAX_VALUE);
    }

    private void login(String accessToken){
        LoginData loginData = new LoginData(accessToken);
        HttpResponse<JsonNode> jsonResponse = null;
        try {
            jsonResponse = Unirest.post(URL.LOGIN.getUrl())
                    .header("accept", "application/json")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body(loginData.toString())
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        JsonNode responseData = jsonResponse.getBody();
        if (checkResponse(responseData)) {
            isLogin = true;

            mResponse = saveLoginData(responseData);

            mBaseData = new PlayData(mResponse);

            System.out.println("Done");
        } else {
            System.out.println("Failure");
            isLogin = false;

        }
    }

    public void handleLogin (ActionEvent actionEvent){
        // Login
        accessToken = token.getText();
        login(accessToken);
        if (isLogin) {
            loggedIn();
        } else {
            loginFailure();
        }
    }

    public ArrayList<Object> play(){
        ArrayList result = new ArrayList<Object>();
        HttpResponse<JsonNode> jsonResponse = null;
        try {
            jsonResponse = Unirest.post(URL.PLAY.getUrl())
                    .header("accept", "application/json")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body(mBaseData.toString())
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        JsonNode responseData = jsonResponse.getBody();
        if (checkResponse(responseData)) {
            JSONObject playData = responseData.getObject().getJSONObject("_d").getJSONObject("data");
            result.add(playData);

            String rewardType = playData.getString("rewardType");
            result.add(rewardType);

            int tili = playData.getInt("tili");
            result.add(tili);
        } else {
            result.add(false);
        }
        return result;
    }
    public Object getFriendList(){
        HttpResponse<JsonNode> jsonResponse = null;
        try {
            jsonResponse = Unirest.post(URL.FR_LIST.getUrl())
                    .header("accept", "application/json")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body(mBaseData.toString())
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        JsonNode responseData = jsonResponse.getBody();
        if (checkResponse(responseData)) {
            return responseData.getObject().getJSONObject("_d");
        } else {
            return false;
        }
    }

    public Object getInfo(int uid){
        HttpResponse<JsonNode> jsonResponse = null;
        try {
            jsonResponse = Unirest.post(URL.INFO.getUrl())
                    .header("accept", "application/json")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body(String.format("%s&puid=%d",mBaseData.toString(), uid))
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        JsonNode responseData = jsonResponse.getBody();
        if (checkResponse(responseData)) {

            return responseData.getObject().getJSONObject("_d");
        } else {
            return false;
        }
    }

    public Object doSteal(int sid) {
        HttpResponse<JsonNode> jsonResponse = null;
        try {
            jsonResponse = Unirest.post(URL.STEAL.getUrl())
                    .header("accept", "application/json")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body(String.format("%s&puid=%d",mBaseData.toString(), sid))
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        JsonNode responseData = jsonResponse.getBody();
        if (checkResponse(responseData)) {

            return responseData.getObject().getJSONObject("_d");
        } else {
            return false;
        }
    }

    // Truong hop khong phải Steal
    private void notSteal(Long countDown){
        sleep(2000); // Dung 2s

        ArrayList spin = spin = play();; // Quay

        // Neu quay thanh cong - Spin successfully:
        if (!spin.get(0).equals(false)) {
            updateMessage(String.format("Bạn nhận được %s. Còn %d lượt quay", spin.get(1), spin.get(2)));
            if (spin.get(1).toString().compareTo("fire") == 0) {
                updateMessage("Vào game để tấn công kẻ địch. Sau đó ở Run Bot để tiếp tục.");
            }
            System.out.println(spin.get(0));

        } else { // Het luot quay - Out of spin

            updateMessage(String.format("Hết lượt quay, còn phải đợi %d", countDown));
        }
    }

    private void isSteal() throws UnirestException, IOException {
        JSONObject stealStatus = new JSONObject();
        String fbpic = mResponse.getJSONObject("zhuanpan").getJSONObject("stealTarget").getString("fbpic");
        if (fbpic.compareTo("") != 0) {
            Long targetId = Long.parseLong(fbpic.split("/")[3]);
            JSONArray targets = mResponse.getJSONArray("stealTarget");
            JSONObject frList = (JSONObject) getFriendList();
            int targetUid = -1;
            for (Object i: frList.getJSONArray("list")) {
                JSONObject jsonI = (JSONObject) i;
                if (targetId == jsonI.getLong("siteuid")) {
                    targetUid = jsonI.getInt("uid");
                }
            }

            if (targetUid != -1) {
                JSONObject targetInfo =  (JSONObject) getInfo(targetUid);
                int targetStar = targetInfo.getJSONObject("planet").getInt("star");
                for (int i = 0; i < targets.length(); i++) {
                    JSONObject target = (JSONObject) targets.get(i);
                    if (target.getJSONObject("planet").getInt("star") == targetStar) {
                        stealStatus = (JSONObject) doSteal(i);
                    }
                }
            } else {
                stealStatus = (JSONObject) doSteal(1);
            }
        } else {
            stealStatus = (JSONObject) doSteal(1);
        }
        System.out.println(stealStatus);
        if (!stealStatus.equals(false)) {
            if (stealStatus.getJSONObject("data").getBoolean("king")) {
                updateMessage("Cướp được Vua");
            } else {
                updateMessage("Không tìm thấy Vua");
            }
        } else {
            updateMessage("Cướp thất bại");
        }
    }

    public void handleRun (ActionEvent actionEvent) throws UnirestException, IOException{
        // Vong lap
        int count = 0;
        while (true) {
            String status = mResponse.getString("status");
            Long countDown = mResponse.getJSONObject("zhuanpan").getLong("time");
            sleep(2000);
            login(accessToken);
            System.out.println(status);
            if (status.compareTo("steal") != 0) {
                notSteal(countDown);
            } else {
                isSteal();
            }
//            sleep(100);
            count++;
            if (count == 3) break;
        }


        // END Vong lap
    }
}
