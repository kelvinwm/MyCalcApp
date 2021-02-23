package com.cilcom.mycalcapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.cilcom.mycalcapp.databinding.ActivityHomeBinding;

import java.util.regex.Pattern;

public class Home extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = Home.class.getSimpleName();
    private ActivityHomeBinding binding;
    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    protected IRemote mService;
    ServiceConnection mServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.btnAdd.setOnClickListener(this);
        binding.btnSubtract.setOnClickListener(this);
        binding.btnDivide.setOnClickListener(this);
        binding.btnMultiply.setOnClickListener(this);

        onTextChangedListener();

        binding.teResult.setEnabled(false);
        binding.teResult.setCursorVisible(false);
        binding.teResult.setKeyListener(null);

        binding.tlResult.setEnabled(false);

        initConnection();
    }

    void initConnection(){
        mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // TODO Auto-generated method stub
                mService = null;
                Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
                Log.d("IRemote", "Binding - Service disconnected");
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // TODO Auto-generated method stub
                mService = IRemote.Stub.asInterface((IBinder) service);
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                Log.d("IRemote", "Binding is done - Service connected");
            }
        };
        if(mService == null) {
            Intent it = new Intent();
            it.setAction("com.remote.service.CALCULATOR");
            it.setPackage(this.getPackageName());
            //binding to remote service
            bindService(it, mServiceConnection, Service.BIND_AUTO_CREATE);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    @Override
    public void onClick(View v) {
        int value1 = Integer.parseInt(binding.teValue1.getText().toString());
        int value2 = Integer.parseInt(binding.teValue2.getText().toString());
        binding.linearLayoutResult.setVisibility(View.VISIBLE);
        hideKeyboard();
        switch (v.getId()){
            case R.id.btnAdd:
                try{
                    binding.teResult.setText("Result -> Add ->"+mService.add(value1, value2));
                    Log.d("IRemote", "Binding - Add operation");
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case R.id.btnSubtract:
                try{
                    binding.teResult.setText("Result -> Subtract ->"+mService.subtract(value1, value2));
                    Log.d("IRemote", "Binding - Subtract operation");
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case R.id.btnDivide:
                try{
                    binding.teResult.setText("Result -> Divide ->"+mService.divide(value1, value2));
                    Log.d("IRemote", "Binding - Divide operation");
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case R.id.btnMultiply:
                try{
                    binding.teResult.setText("Result -> Multiply ->"+mService.multiply(value1, value2));
                    Log.d("IRemote", "Binding - Multiply operation");
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void onTextChangedListener() {
        binding.teValue1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.linearLayoutResult.setVisibility(View.GONE);
                String input = binding.teValue1.getText().toString().trim();
                validateValue1(input);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.teValue2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.linearLayoutResult.setVisibility(View.GONE);
                String input = binding.teValue2.getText().toString().trim();
                validateValue2(input);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validateValue1(String input){
        if (TextUtils.isEmpty(input)) {
            binding.tlValue1.setError("Please input value 1");
            binding.teValue1.setBackground(ContextCompat.getDrawable(Home.this, R.drawable.error_input_background));
            return false;
        } else if (!isNumeric(input)) {
            binding.tlValue1.setError("Please input valid value 1");
            binding.teValue1.setBackground(ContextCompat.getDrawable(Home.this, R.drawable.error_input_background));
            return false;
        } else {
            binding.tlValue1.setError(null);
            binding.tlValue1.setErrorEnabled(false);
            binding.teValue1.setBackground(ContextCompat.getDrawable(Home.this, R.drawable.input_background));
            return true;
        }
    }

    private boolean validateValue2(String input){
        if (TextUtils.isEmpty(input)) {
            binding.tlValue2.setError("Please input value 2");
            binding.teValue2.setBackground(ContextCompat.getDrawable(Home.this, R.drawable.error_input_background));
            return false;
        } else if (!isNumeric(input)) {
            binding.tlValue2.setError("Please input valid value 2");
            binding.teValue2.setBackground(ContextCompat.getDrawable(Home.this, R.drawable.error_input_background));
            return false;
        } else {
            binding.tlValue2.setError(null);
            binding.tlValue2.setErrorEnabled(false);
            binding.teValue2.setBackground(ContextCompat.getDrawable(Home.this, R.drawable.input_background));
            return true;
        }
    }

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    private void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (this.getCurrentFocus() != null && inputManager != null) {
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            inputManager.hideSoftInputFromInputMethod(this.getCurrentFocus().getWindowToken(), 0);
        }
    }
}