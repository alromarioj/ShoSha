package es.shosha.shosha;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import es.shosha.shosha.AdaptadorLista.Productos.ProductosAdapter;
import es.shosha.shosha.AdaptadorLista.Productos.RecyclerViewOnItemClickListener;
import es.shosha.shosha.dominio.Item;
import es.shosha.shosha.dominio.Lista;


import es.shosha.shosha.persistencia.ItemPers;
import es.shosha.shosha.persistencia.ListaPers;
import es.shosha.shosha.persistencia.sqlite.AdaptadorBD;

public class ListaProductos extends AppCompatActivity {
    private ListView list;
    private Lista lista;
    private List<Item> productos;//=new ArrayList<>();
    RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.lista=(Lista)this.getIntent().getExtras().getSerializable("lista");//Se recoge la lista que se ha pasado desde ListasActivas
        productos=lista.getItems();
        System.out.println("Número de productos: "+productos.size());
       // productos.add(new Item("ref01","Tomate",1.5));
        //productos.add(new Item("ref02","Macarrones",2.06));

        setContentView(R.layout.activity_lista_productos);

        mRecyclerView= (RecyclerView) findViewById(R.id.recycler_view);
        setUpRecyclerView(productos);

        //Cambia el título de la página que muestra la lista de productos
        final Toolbar tb = (Toolbar) findViewById(R.id.toolbar2);
        tb.setTitle(lista.getNombre());
        //Aparece el botón de atrás
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        super.onCreate(savedInstanceState);
    }
    public void editarProducto(View view, int position){
            final Item producto=((ProductosAdapter)mRecyclerView.getAdapter()).getItem(position);
            AlertDialog.Builder builder1;
            //Se crea el PopUp para añadir un nuevo producto
            builder1=new AlertDialog.Builder(this);
            builder1.setTitle("Editar producto");

            View viewInflated1 = LayoutInflater.from(getBaseContext()).inflate(R.layout.nuevo_producto, (ViewGroup) findViewById(android.R.id.content), false);
            // Set up the input
            final EditText input_np2 = (EditText) viewInflated1.findViewById(R.id.in_nombre_producto);
            input_np2.setText(producto.getNombre());
            final EditText input_pp = (EditText) viewInflated1.findViewById(R.id.in_precio_producto);
            input_pp.setText(String.valueOf(producto.getPrecio()));

            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            builder1.setView(viewInflated1);

            // Set up the buttons
            builder1.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Asumiendo que el precio es >=0
                    AdaptadorBD abd = new AdaptadorBD(getBaseContext());
                    abd.open();
                    //new ItemPers(MyApplication.getAppContext()).execute("insert", id, MyApplication.getUser().getId());
                    //Se inserta un producto a la lista a partir de los datos introducidos
                    String precio=input_pp.getText().toString();
                    precio=(precio.isEmpty()?"0":precio);
                    //Item i=new Item("ref"+lista.getItems().size(),input_np2.getText().toString(),Double.valueOf(precio));
                    producto.setNombre(input_np2.getText().toString());
                    producto.setPrecio(Double.valueOf(precio));

                    //new ListaPers(MyApplication.getAppContext(), null).execute("update", id, MyApplication.getUser().getId());
                    abd.insertarItem(producto.getId(),producto.getNombre(),producto.getPrecio(),lista.getId());
                    abd.close();
                    Toast.makeText(ListaProductos.this, "Editando producto " + producto.getNombre(), Toast.LENGTH_SHORT).show();
                    //Avisa de que la lista ha cambiado
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                    dialog.dismiss();
                }
            });
            builder1.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder1.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Mostrar menú para la lista de productos
        getMenuInflater().inflate(R.menu.menu_lista_productos,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.anadir_producto:
                //Se crea el PopUp para añadir un nuevo producto
                AlertDialog.Builder builder;
                View viewInflated;
                builder=new AlertDialog.Builder(this);
                builder.setTitle("Añadir nuevo producto");

                viewInflated = LayoutInflater.from(getBaseContext()).inflate(R.layout.nuevo_producto, (ViewGroup) findViewById(android.R.id.content), false);
                // Set up the input
                final EditText input_np1 = (EditText) viewInflated.findViewById(R.id.in_nombre_producto);
                final EditText input_pp = (EditText) viewInflated.findViewById(R.id.in_precio_producto);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(viewInflated);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Asumiendo que el precio es >=0
                        AdaptadorBD abd = new AdaptadorBD(getBaseContext());
                        abd.open();
                        //new ItemPers(MyApplication.getAppContext()).execute("insert", id, MyApplication.getUser().getId());
                        //Se inserta un producto a la lista a partir de los datos introducidos
                        String precio=input_pp.getText().toString();
                        precio=(precio.isEmpty()?"0":precio);
                        Item i=new Item("ref"+lista.getItems().size(),input_np1.getText().toString(),Double.valueOf(precio));
                        productos.add(i);
                        abd.insertarItem(i.getId(),i.getNombre(),i.getPrecio(),lista.getId());
                        //Insertar con listaPers
                        new ItemPers(MyApplication.getAppContext()).execute("insert", lista.getId(), i.getNombre(),String.valueOf(i.getPrecio()),"1");
                        abd.close();
                        Toast.makeText(ListaProductos.this, "Añadiendo producto " + i.getNombre(), Toast.LENGTH_SHORT).show();
                        //Avisa de que la lista ha cambiado
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void setUpRecyclerView(List<Item> productos) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new ProductosAdapter(productos, new RecyclerViewOnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                editarProducto(v,position);
            }
        }));
        //mRecyclerView.setHasFixedSize(true);
        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();
    }
    /**
     * This is the standard support library way of implementing "swipe to delete" feature. You can do custom drawing in onChildDraw method
     * but whatever you draw will disappear once the swipe is over, and while the items are animating to their new position the recycler view
     * background will be visible. That is rarely an desired effect.
     */
    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(getResources().getColor(R.color.colorPrimary));
                xMark = ContextCompat.getDrawable(ListaProductos.this, R.drawable.eliminar);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) ListaProductos.this.getResources().getDimension(R.dimen.fab_margin);
                initiated = true;
            }
            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                ProductosAdapter testAdapter = (ProductosAdapter)recyclerView.getAdapter();
                if (testAdapter.isUndoOn() && testAdapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                ProductosAdapter adapter = (ProductosAdapter)mRecyclerView.getAdapter();
                boolean undoOn = adapter.isUndoOn();
                if (undoOn) {
                    adapter.pendingRemoval(swipedPosition);
                } else {
                    AdaptadorBD abd = new AdaptadorBD(getBaseContext());
                    abd.open();
                    //Eliminar producto con el adaptador de la base de datos

                    adapter.remove(swipedPosition);
                    abd.close();
                    Toast.makeText(ListaProductos.this, "Eliminando producto ", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }
                if (!initiated) {
                    init();
                }
                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    /**
     * We're gonna setup another ItemDecorator that will draw the red background in the empty space while the items are animating to thier new positions
     * after an item is removed.
     */
    private void setUpAnimationDecoratorHelper() {
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                if (!initiated) {
                    init();
                }
                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }

}
