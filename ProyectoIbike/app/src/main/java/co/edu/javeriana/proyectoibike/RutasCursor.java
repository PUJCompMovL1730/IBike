package co.edu.javeriana.proyectoibike;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Cristian on 20/11/2017.
 */

public class RutasCursor extends CursorAdapter {

    private static final int CONTACT_ID_INDEX = 0;
    private static final int DISPLAY_NAME_INDEX = 1;
    private static final int DISPLAY_NAME_INDEX2 = 2;

    public RutasCursor(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_listview,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView textoDestino = (TextView) view.findViewById(R.id.textoDestino);
        TextView textoFecha = (TextView) view.findViewById(R.id.textoFecha);
        TextView textoMetros = (TextView) view.findViewById(R.id. textoMetros);

        String nombreDestino = cursor.getString(CONTACT_ID_INDEX);
        String Fecha = cursor.getString(DISPLAY_NAME_INDEX);
        String metros = cursor.getString(DISPLAY_NAME_INDEX2);

        textoDestino.setText(nombreDestino);
        textoFecha.setText(Fecha);
        textoMetros.setText(metros);
    }
}
