package com.veritran.system.bank.app;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.veritran.system.bank.app.enums.TypeMessagesEnum;
import com.veritran.system.bank.app.utils.MessagesUtil;
import com.veritran.system.single.bank.commons.dto.DepositoDTO;
import com.veritran.system.single.bank.commons.enums.FuncionalidadEnum;
import com.veritran.system.single.bank.commons.service.impl.DepositoServiceImpl;
import com.veritran.system.single.bank.commons.util.CambiaFormatoNumero;
import com.veritran.system.single.bank.commons.util.FechaUtil;

import java.time.LocalDateTime;

public class ActivityZonaTransaccional extends AppCompatActivity implements View.OnClickListener {

    private EditText textValorDeposito;
    private EditText textNumeroCtaDestino;

    private TextView textTitleOperacion;
    private TextView textCliente;
    private TextView textCuentaOrigen;
    private TextView textSaldoActual;
    private TextView textFechaTransaccion;

    private Button btnConfirmar;
    private Button btnRegresar;

    FuncionalidadEnum funcionalidadEnum;
    DepositoDTO depositoDTO = new DepositoDTO();
    DepositoServiceImpl depositoService = new DepositoServiceImpl();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zona_transaccional);

        textNumeroCtaDestino = (EditText) findViewById(R.id.editCuentaDestino);
        textValorDeposito = (EditText) findViewById(R.id.editValorDeposito);
        textValorDeposito.setText("0");
        textNumeroCtaDestino.setEnabled(false);
        textTitleOperacion = (TextView) findViewById(R.id.textTitleOperacion);
        textCliente = (TextView) findViewById(R.id.textViewCliente);
        textCuentaOrigen = (TextView) findViewById(R.id.textCuentaOrigen);
        textSaldoActual = (TextView) findViewById(R.id.textSaldoActual);
        textFechaTransaccion = (TextView) findViewById(R.id.textFechaTransaccion);
        textFechaTransaccion.setText(FechaUtil.localDateTimeToString(FechaUtil.FORMATO_FECHA_HORA, LocalDateTime.now()));

        btnRegresar = (Button) findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(this);

        btnConfirmar = (Button) findViewById(R.id.btnConfirmar);
        btnConfirmar.setOnClickListener(this);

        Intent intentZona = getIntent();
        Bundle bundle = intentZona.getExtras();

        if (bundle != null){
            String nombreCliente = (String) bundle.get("Cliente");
            String numeroCuenta = (String) bundle.get("NumeroCuenta");
            String saldoActual = (String) bundle.get("SaldoActual");
            this.funcionalidadEnum = (FuncionalidadEnum) bundle.get("FuncionalidadEnum");
            depositoDTO.setCliente(nombreCliente);
            depositoDTO.setCuentaOrigen(numeroCuenta);
            depositoDTO.setValorSaldoActual(Integer.parseInt(saldoActual));
            operacionZonal();
        }

    }

    private void operacionZonal() {
        textTitleOperacion.setText("Operación a realizar: " + funcionalidadEnum.getTituloFuncionalida());
        textCliente.setText("Estimado Sr(a): " + depositoDTO.getCliente());
        textCuentaOrigen.setText("N° de Cuenta origen: " + depositoDTO.getCuentaOrigen());
        textSaldoActual.setText("Su Saldo actual: $ " + CambiaFormatoNumero.numerico(depositoDTO.getValorSaldoActual()));
        if (FuncionalidadEnum.TRANSFERENCIA_SALDO.equals(funcionalidadEnum)) {
            textNumeroCtaDestino.setEnabled(true);
        }
        textNumeroCtaDestino.setText("");
        textValorDeposito.setText("0");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnConfirmar:
                ejecutarOperacion();
                break;

            case R.id.btnRegresar:
                startActivity(new Intent(this, MainActivity.class));
                break;

            default:
                break;
        }
    }

    private void ejecutarOperacion() {
        switch (funcionalidadEnum){
            case RETIRO_SALDO:
                operacionRetiro();
                break;
            case ADICIONAR_SALDO:
                operacionAdicion();
                break;
            case TRANSFERENCIA_SALDO:
                operacionTransferencia();
                break;
            default:
                break;
        }
    }

    private void operacionTransferencia() {
        Integer valorDeposito = !textValorDeposito.getText().toString().isEmpty() ?
                Integer.parseInt(textValorDeposito.getText().toString()) : 0;
        if (valorDeposito > 0) {
            depositoDTO.setCuentaDestino(textNumeroCtaDestino.getText().toString());
            if (depositoDTO.getCuentaDestino().isEmpty()) {
                MessagesUtil.getMessage(getApplicationContext(),
                        "Por favor ingrese el número de la cuenta destino.",
                        TypeMessagesEnum.WARNING);
            } else if (Integer.parseInt(depositoDTO.getCuentaDestino()) < 0) {
                MessagesUtil.getMessage(getApplicationContext(),
                        "Por favor ingrese un número de cuenta destino valido.",
                        TypeMessagesEnum.WARNING);
            } else if (valorDeposito > depositoDTO.getValorSaldoActual()) {
                MessagesUtil.getMessage(getApplicationContext(),
                        "Sr(a): " + depositoDTO.getCliente() + "\n" +
                                "Su saldo actual es insuficiente para hacer la transferencia.",
                        TypeMessagesEnum.WARNING);
            } else {
                depositoDTO.setValorDeposito(valorDeposito);
                depositoService.transferenciaDeposito(depositoDTO);
                MessagesUtil.getMessage(getApplicationContext(),
                        "Su transferencia se realizó correctamente.",
                        TypeMessagesEnum.INFO);
                operacionZonal();
            }
        } else {
            textValorDeposito.setText("0");
            MessagesUtil.getMessage(getApplicationContext(),
                    "El valor de la transferencia debe ser mayor a cero.",
                    TypeMessagesEnum.WARNING);
        }

    }

    private void operacionAdicion() {
        Integer valorDeposito = !textValorDeposito.getText().toString().isEmpty() ?
                Integer.parseInt(textValorDeposito.getText().toString()) : 0;
        if (valorDeposito > 0) {
            depositoDTO.setValorDeposito(valorDeposito);
            depositoDTO = depositoService.agregarDeposito(depositoDTO);
            MessagesUtil.getMessage(getApplicationContext(),
                    "Su adición de saldo se realizó correctamente.",
                    TypeMessagesEnum.INFO);
            operacionZonal();

        } else {
            textValorDeposito.setText("0");
            MessagesUtil.getMessage(getApplicationContext(),
                    "El valor del deposito debe ser mayor a cero.",
                    TypeMessagesEnum.WARNING);
        }

    }

    private void operacionRetiro() {
        Log.i("ACCION Retiro","INGRESO A OPERACION RETIRO.");
        Integer valorDeposito = !textValorDeposito.getText().toString().isEmpty() ?
                Integer.parseInt(textValorDeposito.getText().toString()) : 0;
        if (valorDeposito > 0) {
            if (valorDeposito <= depositoDTO.getValorSaldoActual()) {
                depositoDTO.setValorDeposito(valorDeposito);
                depositoDTO = depositoService.retirarDeposito(depositoDTO);
                MessagesUtil.getMessage(getApplicationContext(),
                        "Su retiro se realizó correctamente.",
                        TypeMessagesEnum.INFO);
                operacionZonal();
            } else {
                MessagesUtil.getMessage(getApplicationContext(),
                        "Sr(a): " + depositoDTO.getCliente() + "\n" +
                                "Su saldo es insuficiente para hacer el retiro solicitado.",
                        TypeMessagesEnum.WARNING);
            }
        } else {
            textValorDeposito.setText("0");
            MessagesUtil.getMessage(getApplicationContext(),
                    "El valor del retiro debe ser mayor a cero.",
                    TypeMessagesEnum.WARNING);
        }

    }
}
