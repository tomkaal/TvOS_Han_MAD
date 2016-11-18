package tvos.mad.han.mijnparkcontroller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by DDulos on 18-Nov-16.
 */
public class UsersInRangeAdapter extends BaseAdapter {
    Context context;
    private LayoutInflater inflater;

    ArrayList<User> userList;

    public UsersInRangeAdapter(Context context, ArrayList<User> userList) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public User getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null)
            view = inflater.inflate(R.layout.list_item_user_group_setup, parent, false);

        User user = getItem(position);
        TextView userNameText = (TextView) view.findViewById(R.id.txt_username);
        userNameText.setText(user.getUsername());

        return view;
    }
}
