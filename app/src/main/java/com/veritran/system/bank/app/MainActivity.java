package com.veritran.system.bank.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.veritran.system.bank.app.enums.TypeMessagesEnum;
import com.veritran.system.bank.app.utils.MessagesUtil;
import com.veritran.system.single.bank.commons.dto.DepositoDTO;
import com.veritran.system.single.bank.commons.enums.FuncionalidadEnum;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    DepositoDTO depositoDTO = new DepositoDTO();
    private EditText textCliente;
    private EditText textNumeroCuenta;
    private EditText textSaldoActual;

    Button btnAdicionarSaldo;
    Button btnRetirarSaldo;
    Button btnTransferirSaldo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textCliente = (EditText) findViewById(R.id.textCliente);
        textNumeroCuenta = (EditText) findViewById(R.id.textNumCuenta);
        textSaldoActual = (EditText) findViewById(R.id.textSaldoActual);
        textSaldoActual.setText("0");
        btnAdicionarSaldo = (Button) findViewById(R.id.btnAdicionarSaldo);
        btnAdicionarSaldo.setOnClickListener(this);
        btnRetirarSaldo = (Button) findViewById(R.id.btnRetirarSaldo);
        btnRetirarSaldo.setOnClickListener(this);
        btnTransferirSaldo = (Button) findViewById(R.id.btnTransferencia);
        btnTransferirSaldo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnAdicionarSaldo:
                zonaTransaccional(FuncionalidadEnum.ADICIONAR_SALDO);
                break;
            case R.id.btnRetirarSaldo:
                zonaTransaccional(FuncionalidadEnum.RETIRO_SALDO);
                break;
            case R.id.btnTransferencia:
                zonaTransaccional(FuncionalidadEnum.TRANSFERENCIA_SALDO);
                break;

            default:
                break;
        }


    }

    private void zonaTransaccional(FuncionalidadEnum funcionalidadEnum) {
        if(textCliente.getText().toString().isEmpty() | textNumeroCuenta.getText().toString().isEmpty()
                | textSaldoActual.getText().toString().isEmpty()){
            MessagesUtil.getMessage(getApplicationContext(),
                    "Por favor ingresa el\n*Nombre del cliente\n*Número de cuenta \n*Saldo actual\n" +
                            "no deben encontrarse vacios.",
                    TypeMessagesEnum.WARNING);
            return;
        }
        Integer valorSaldoActual = Integer.parseInt(textSaldoActual.getText().toString());
        Integer numeroCuenta = Integer.parseInt(textNumeroCuenta.getText().toString());
        if (numeroCuenta < 0) {
            MessagesUtil.getMessage(getApplicationContext(),
                    "Por favor ingrese un número de cuenta valido.",
                    TypeMessagesEnum.WARNING);
        } else if (valorSaldoActual < 0 ) {
            MessagesUtil.getMessage(getApplicationContext(),
                    "El saldo actual debe ser mayor o igual a cero.",
                    TypeMessagesEnum.WARNING);
        } else if (valorSaldoActual <= 0 &&
                funcionalidadEnum.equals(FuncionalidadEnum.RETIRO_SALDO) |
                        funcionalidadEnum.equals(FuncionalidadEnum.TRANSFERENCIA_SALDO)){
            MessagesUtil.getMessage(getApplicationContext(),
                    "Usted no tiene saldo disponible para realizar esta operación.",
                    TypeMessagesEnum.WARNING);
        } else if(!textCliente.getText().toString().isEmpty() && !textNumeroCuenta.getText().toString().isEmpty()
                && !textSaldoActual.getText().toString().isEmpty()){

            MessagesUtil.getMessage(getApplicationContext(),
                    "Bienvenido a la zona transaccional vamos a: " + funcionalidadEnum.getTituloFuncionalida(),
                    TypeMessagesEnum.INFO);

            Intent intentZonaTransaccional = new Intent(this, ActivityZonaTransaccional.class);
            intentZonaTransaccional.putExtra("Cliente", textCliente.getText().toString());
            intentZonaTransaccional.putExtra("NumeroCuenta", textNumeroCuenta.getText().toString());
            intentZonaTransaccional.putExtra("SaldoActual", textSaldoActual.getText().toString());
            intentZonaTransaccional.putExtra("FuncionalidadEnum", funcionalidadEnum);

            startActivity(intentZonaTransaccional);
        }
    }
}
