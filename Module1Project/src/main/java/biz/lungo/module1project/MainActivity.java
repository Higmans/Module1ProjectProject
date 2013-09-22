package biz.lungo.module1project;

import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.widget.*;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {
    private static final int DATE = 0;
    private static final int TIME = 1;
    public static final String ERROR = "!ERROR!";
    Activity activity;
    LinearLayout registerForm;
    TextView textViewWelcome;
    TextView textViewReadFromName;
    TextView textViewReadFromEmail;
    TextView textViewReadFromSubject;
    TextView textViewReadFromMessage;
    EditText editTextRegisterName;
    EditText editTextRegisterSurname;
    EditText editTextRegisterEmail;
    EditText editTextSendMessageName;
    EditText editTextSendMessageSurname;
    EditText editTextSendMessageEmail;
    EditText editTextSendMessageSubject;
    EditText editTextSendMessageMessage;
    EditText editTextReadMessage;
    Button register;
    Button registerSave;
    Button sendMessage;
    Button readMessage;
    SharedPreferences sharedPreferencesUserData;
    JSONObject message;
    String userName;
    String userSurname;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        registerForm = (LinearLayout) findViewById(R.id.registerForm);
        textViewWelcome = (TextView) findViewById(R.id.textViewWelcome);
        textViewReadFromName = (TextView) findViewById(R.id.textViewReadFromName);
        textViewReadFromEmail = (TextView) findViewById(R.id.textViewReadFromEmail);
        textViewReadFromSubject = (TextView) findViewById(R.id.textViewReadFromSubject);
        textViewReadFromMessage = (TextView) findViewById(R.id.textViewReadFromMessage);
        editTextRegisterName = (EditText) findViewById(R.id.editTextRegisterName);
        editTextRegisterSurname = (EditText) findViewById(R.id.editTextRegisterSurname);
        editTextRegisterEmail = (EditText) findViewById(R.id.editTextRegisterEmail);
        editTextSendMessageName = (EditText) findViewById(R.id.editTextSendName);
        editTextSendMessageSurname = (EditText) findViewById(R.id.editTextSendSurname);
        editTextSendMessageEmail = (EditText) findViewById(R.id.editTextSendEmail);
        editTextSendMessageSubject = (EditText) findViewById(R.id.editTextSendSubject);
        editTextSendMessageMessage = (EditText) findViewById(R.id.editTextSendMessage);
        editTextReadMessage = (EditText) findViewById(R.id.editTextReadMessage);
        register = (Button) findViewById(R.id.buttonRegister);
        registerSave = (Button) findViewById(R.id.buttonRegisterSave);
        sendMessage = (Button) findViewById(R.id.buttonSendMessagge);
        readMessage = (Button) findViewById(R.id.buttonReadMessage);
        message = new JSONObject();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideRegisterForm(false);
            }
        });

        registerSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (saveData()){
                    hideRegisterForm(true);
                    Toast.makeText(activity, "Данные сохранены", Toast.LENGTH_SHORT).show();
                    textViewWelcome.setText("Вы вошли как " +
                            editTextRegisterName.getText() + " " +
                            editTextRegisterSurname.getText());
                }
            }
        });

        readMessage.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View view) {
                if (!editTextReadMessage.getText().toString().equals("")){
                    if (checkJSON()){
                        fillReadFields();
                    }
                    else
                        showAlert("Неверная строка JSON!");
                }
                else
                    showAlert("Нечего читать! Введите сообщение");
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View view) {
                if (!isUserRegistered()) {
                    showAlert("Вы не зарегистрированы!");
                } else {
                    if (checkSendForm()){
                        try {
                            message.put("from_name", userName)
                                    .put("from_lastname", userSurname)
                                    .put("from_email", userEmail)
                                    .put("to_name", editTextSendMessageName.getText().toString())
                                    .put("to_lastname", editTextSendMessageSurname.getText().toString())
                                    .put("to_email", editTextSendMessageEmail.getText().toString())
                                    .put("theme", editTextSendMessageSubject.getText().toString())
                                    .put("message", editTextSendMessageMessage.getText().toString())
                                    .put("date", getTime(DATE))
                                    .put("time", getTime(TIME));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        showMessageDialog();
                    }
                }
            }
        });

        if (isUserRegistered()){
            sharedPreferencesUserData = getSharedPreferences("user_data", MODE_PRIVATE);
            String name = sharedPreferencesUserData.getString("Name", "");
            userName = name;
            String surname = sharedPreferencesUserData.getString("Surname", "");
            userSurname = surname;
            String email = sharedPreferencesUserData.getString("Email", "");
            userEmail = email;
            editTextRegisterName.setText(name);
            editTextRegisterSurname.setText(surname);
            editTextRegisterEmail.setText(email);
            hideRegisterForm(true);
            textViewWelcome.setText("Вы вошли как " + name + " " + surname);
        }
        else {
            textViewWelcome.setText("Вы не зарегистрированы!");
            register.setEnabled(false);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void fillReadFields() {
        String fromName;
        String fromLastname;
        String time;
        String date;
        JSONObject readMessage = null;
        try {
            readMessage = new JSONObject(editTextReadMessage.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            fromName = readMessage.getString("from_name");
        } catch (JSONException e) {
            fromName = ERROR;
        }
        try {
            fromLastname = readMessage.getString("from_lastname");
        } catch (JSONException e) {
            fromLastname = ERROR;
        }
        textViewReadFromName.setText(fromName + " " + fromLastname);
        try {
            textViewReadFromEmail.setText(readMessage.getString("from_email"));
        } catch (JSONException e) {
            textViewReadFromEmail.setText(ERROR);
        }
        try {
            textViewReadFromSubject.setText(readMessage.getString("theme"));
        } catch (JSONException e) {
            textViewReadFromSubject.setText(ERROR);
        }
        try {
            time = readMessage.getString("time");
        } catch (JSONException e) {
            time = ERROR;
        }
        try {
            date = readMessage.getString("date");
        } catch (JSONException e) {
            date = ERROR;
        }
        try {
            textViewReadFromMessage.setText(readMessage.getString("message") + "\n" + time + " " + date);
        } catch (JSONException e) {
            textViewReadFromMessage.setText(ERROR);
        }

    }

    @SuppressWarnings("ConstantConditions")
    private boolean checkJSON() {
        boolean valid;
        try {
            new JSONObject(editTextReadMessage.getText().toString());
            valid = true;
        } catch (JSONException e) {
            valid = false;
        }
        return valid;
    }

    private String getTime(int format) {
        Date date = new Date();
        String dateString = new SimpleDateFormat("dd/MM/yyyy").format(date);
        String timeString = new SimpleDateFormat("HH:mm:ss").format(date);
        String output = "";
        switch (format){
            case DATE:
                output = dateString;
                break;
            case TIME:
                output = timeString;
                break;
        }
        return output;
    }

    private void showMessageDialog() {
        TextView dialogView = new TextView(activity);
        dialogView.setText(message.toString());
        dialogView.setTextIsSelectable(true);
        AlertDialog.Builder messageDialog = new AlertDialog.Builder(activity);
        messageDialog.setTitle("Сообщение");
        messageDialog.setView(dialogView);
        messageDialog.setPositiveButton("Копировать в буфер", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Message", message.toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(activity, "Сообщение скопировано в буфер", Toast.LENGTH_SHORT).show();
            }
        });
        messageDialog.setNegativeButton("Закрыть", null);
        messageDialog.create().show();
    }

    @SuppressWarnings("ConstantConditions")
    private boolean checkSendForm() {
        if (editTextSendMessageName.getText().toString().equals("")){
            showAlert("Вы не заполнили поле \"Имя\" в форме отправки");
            return false;
        }
        if (editTextSendMessageSurname.getText().toString().equals("")){
            showAlert("Вы не заполнили поле \"Фамилия\" в форме отправки");
            return false;
        }
        if (editTextSendMessageEmail.getText().toString().equals("")){
            showAlert("Вы не заполнили поле \"E-mail\" в форме отправки");
            return false;
        }
        if (!checkEmail(editTextSendMessageEmail.getText().toString())){
            showAlert("Неверно введен E-mail получателя");
            return false;
        }
        return true;
    }

    private void hideRegisterForm(boolean hide) {
        if (hide){
            registerForm.setVisibility(View.GONE);
            registerSave.setVisibility(View.GONE);
            register.setEnabled(true);
        }
        else{
            registerForm.setVisibility(View.VISIBLE);
            registerSave.setVisibility(View.VISIBLE);
            register.setEnabled(false);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private boolean saveData() {
        if (!editTextRegisterName.getText().toString().equals("") &&
                !editTextRegisterSurname.getText().toString().equals("") &&
                checkEmail(editTextRegisterEmail.getText().toString())){
            sharedPreferencesUserData = getSharedPreferences("user_data", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencesUserData.edit();
            editor.putString("Name", editTextRegisterName.getText().toString());
            editor.putString("Surname", editTextRegisterSurname.getText().toString());
            editor.putString("Email", editTextRegisterEmail.getText().toString());
            editor.apply();
            return true;
        }
        else{
            if (editTextRegisterName.getText().toString().equals("")){
                showAlert("Вы не заполнили поле \"Имя\"");
                return false;
            }
            if (editTextRegisterSurname.getText().toString().equals("")){
                showAlert("Вы не заполнили поле \"Фамилия\"");
                return false;
            }
            if (editTextRegisterEmail.getText().toString().equals("")){
                showAlert("Вы не заполнили поле \"E-mail\"");
                return false;
            }
            if (!checkEmail(editTextRegisterEmail.getText().toString())){
                showAlert("Ошибка в поле \"E-mail\"");
                return false;
            }
        }
        return false;
    }

    private void showAlert(String alert) {
        AlertDialog.Builder alb = new AlertDialog.Builder(this);
        alb.setTitle("Ошибка!");
        alb.setMessage(alert);
        alb.setPositiveButton("OK", null);
        alb.create().show();
    }

    private boolean checkEmail(String email){
        Pattern p = Pattern.compile("^[-a-z0-9!#$%&'*+/=?^_`{|}~]+(?:\\.[-a-z0-9!#$%&'*+/=?^_`{|}~]+)*@(?:[a-z0-9]([-a-z0-9]{0,61}[a-z0-9])?\\.)*(?:aero|arpa|asia|biz|cat|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|net|org|pro|tel|travel|[a-z][a-z])$");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    private boolean isUserRegistered() {
        sharedPreferencesUserData = getSharedPreferences("user_data", MODE_PRIVATE);
        String name = sharedPreferencesUserData.getString("Name", "");
        return !name.equals("");
    }

    @SuppressWarnings({"ConstantConditions", "NullableProblems"})
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("from_name", textViewReadFromName.getText().toString());
        outState.putString("from_email", textViewReadFromEmail.getText().toString());
        outState.putString("subject", textViewReadFromSubject.getText().toString());
        outState.putString("message", textViewReadFromMessage.getText().toString());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        textViewReadFromName.setText(savedInstanceState.getString("from_name"));
        textViewReadFromEmail.setText(savedInstanceState.getString("from_email"));
        textViewReadFromSubject.setText(savedInstanceState.getString("subject"));
        textViewReadFromMessage.setText(savedInstanceState.getString("message"));
    }
}