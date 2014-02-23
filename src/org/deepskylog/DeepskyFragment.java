package org.deepskylog;

import org.deepskylog.GetDslCommand.GetDslCommandOnResult;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class DeepskyFragment extends Fragment {

	private Bundle savedState = null;

	private View deepskyFragmentView;
	private TextView text1_textview;
	private TextView text2_textview;
	
	private String test="[{\"observationid\":\"1000\",\"objectname\":\"NGC 6563\",\"observationdate\":\"20040722\",\"observationdescription\":\"Zeerhelder maar niet zo groot. Bij perifeer kijken een donutvorm.\",\"observerid\":\"bernd\",\"observername\":\"Bernd Van Papegem\",\"observersortname\":\"Van Papegem Bernd\",\"objectconstellation\":\"SGR\",\"objecttype\":\"PLNNB\",\"objectmagnitude\":\"10.68\",\"objectsurfacebrigthness\":\"9.91\",\"instrumentid\":\"1046\",\"instrumentname\":\"TAL-2M\",\"instrumentdiameter\":\"150\",\"instrumentsort\":\"10150 mm TAL-2M\"},{\"observationid\":\"1001\",\"objectname\":\"NGC 6629\",\"observationdate\":\"20040722\",\"observationdescription\":\"Zeer helder wel niet al te groot, Bij perifeer kijken: donutvorm.\",\"observerid\":\"bernd\",\"observername\":\"Bernd Van Papegem\",\"observersortname\":\"Van Papegem Bernd\",\"objectconstellation\":\"SGR\",\"objecttype\":\"PLNNB\",\"objectmagnitude\":\"11.05\",\"objectsurfacebrigthness\":\"7.85\",\"instrumentid\":\"1046\",\"instrumentname\":\"TAL-2M\",\"instrumentdiameter\":\"150\",\"instrumentsort\":\"10150 mm TAL-2M\"},{\"observationid\":\"1002\",\"objectname\":\"NGC 672\",\"observationdate\":\"20040722\",\"observationdescription\":\"Is wat langwerpig met eerder onderaan heldere kern. Tof! Galaxy.\",\"observerid\":\"bernd\",\"observername\":\"Bernd Van Papegem\",\"observersortname\":\"Van Papegem Bernd\",\"objectconstellation\":\"TRI\",\"objecttype\":\"GALXY\",\"objectmagnitude\":\"10.6\",\"objectsurfacebrigthness\":\"13.56\",\"instrumentid\":\"1046\",\"instrumentname\":\"TAL-2M\",\"instrumentdiameter\":\"150\",\"instrumentsort\":\"10150 mm TAL-2M\"},{\"observationid\":\"1003\",\"objectname\":\"M 11\",\"observationdate\":\"20040712\",\"observationdescription\":\"…Èn heel heldere centrale ster, zeer opvallend, makkelijk te vinden, open cluster :-((\",\"observerid\":\"Ward De Jonghe\",\"observername\":\"Ward De Jonghe\",\"observersortname\":\"De Jonghe Ward\",\"objectconstellation\":\"SCT\",\"objecttype\":\"OPNCL\",\"objectmagnitude\":\"5.8\",\"objectsurfacebrigthness\":\"11.27\",\"instrumentid\":\"1072\",\"instrumentname\":\"Sky-watcher 20cm\",\"instrumentdiameter\":\"200\",\"instrumentsort\":\"10200 mm Sky-watcher 20cm\"},{\"observationid\":\"1004\",\"objectname\":\"NGC 6818\",\"observationdate\":\"20040722\",\"observationdescription\":\"little gem. De little is wel echt groot en vooral super helder! Is, denk ik, de best bezienbare planetaire nevel, die ik ooit al gezien heb.\",\"observerid\":\"bernd\",\"observername\":\"Bernd Van Papegem\",\"observersortname\":\"Van Papegem Bernd\",\"objectconstellation\":\"SGR\",\"objecttype\":\"PLNNB\",\"objectmagnitude\":\"9.22\",\"objectsurfacebrigthness\":\"6.57\",\"instrumentid\":\"1046\",\"instrumentname\":\"TAL-2M\",\"instrumentdiameter\":\"150\",\"instrumentsort\":\"10150 mm TAL-2M\"},{\"observationid\":\"1005\",\"objectname\":\"M 57\",\"observationdate\":\"20040712\",\"observationdescription\":\"duidelijke ring, maar toch niet volledig rond, eerder ovaal, \",\"observerid\":\"Ward De Jonghe\",\"observername\":\"Ward De Jonghe\",\"observersortname\":\"De Jonghe Ward\",\"objectconstellation\":\"LYR\",\"objecttype\":\"PLNNB\",\"objectmagnitude\":\"9\",\"objectsurfacebrigthness\":\"9.25\",\"instrumentid\":\"1072\",\"instrumentname\":\"Sky-watcher 20cm\",\"instrumentdiameter\":\"200\",\"instrumentsort\":\"10200 mm Sky-watcher 20cm\"},{\"observationid\":\"1006\",\"objectname\":\"NGC 925\",\"observationdate\":\"20040722\",\"observationdescription\":\"Super groot! De kern is zeer makkelijk te zien. En bij perifeer kijken rijken de stofwolken echt ver.\",\"observerid\":\"bernd\",\"observername\":\"Bernd Van Papegem\",\"observersortname\":\"Van Papegem Bernd\",\"objectconstellation\":\"TRI\",\"objecttype\":\"GALXY\",\"objectmagnitude\":\"9.9\",\"objectsurfacebrigthness\":\"14.21\",\"instrumentid\":\"1046\",\"instrumentname\":\"TAL-2M\",\"instrumentdiameter\":\"150\",\"instrumentsort\":\"10150 mm TAL-2M\"},{\"observationid\":\"1007\",\"objectname\":\"M 27\",\"observationdate\":\"20040713\",\"observationdescription\":\"grote licht grijze waas met in het midden een duidlijk donkerder gedeelte(haltervorm), niet gezien in de zoeker.\",\"observerid\":\"Ward De Jonghe\",\"observername\":\"Ward De Jonghe\",\"observersortname\":\"De Jonghe Ward\",\"objectconstellation\":\"VUL\",\"objecttype\":\"PLNNB\",\"objectmagnitude\":\"7\",\"objectsurfacebrigthness\":\"10.87\",\"instrumentid\":\"1072\",\"instrumentname\":\"Sky-watcher 20cm\",\"instrumentdiameter\":\"200\",\"instrumentsort\":\"10200 mm Sky-watcher 20cm\"},{\"observationid\":\"1008\",\"objectname\":\"M 71\",\"observationdate\":\"20040713\",\"observationdescription\":\"makkelijk te vinden, maar toch zwakjes. Alle sterren zijn ongeveer even helder, niet allemaal losse sterren, eerder wazig soms. Sommigne beweren dat het een open cluster is, maarik ben er van overtuigd dat het een bolleke is(anders zou ik er niet naar kijken)\r\n\",\"observerid\":\"Ward De Jonghe\",\"observername\":\"Ward De Jonghe\",\"observersortname\":\"De Jonghe Ward\",\"objectconstellation\":\"SGE\",\"objecttype\":\"GLOCL\",\"objectmagnitude\":\"8.2\",\"objectsurfacebrigthness\":\"11.86\",\"instrumentid\":\"1072\",\"instrumentname\":\"Sky-watcher 20cm\",\"instrumentdiameter\":\"200\",\"instrumentsort\":\"10200 mm Sky-watcher 20cm\"},{\"observationid\":\"1009\",\"objectname\":\"NGC 3147\",\"observationdate\":\"20040720\",\"observationdescription\":\"Heldere kern die afzwakt naar buiten toe. Heeft geen echte vorm.\",\"observerid\":\"bernd\",\"observername\":\"Bernd Van Papegem\",\"observersortname\":\"Van Papegem Bernd\",\"objectconstellation\":\"DRA\",\"objecttype\":\"GALXY\",\"objectmagnitude\":\"10.6\",\"objectsurfacebrigthness\":\"13.34\",\"instrumentid\":\"1046\",\"instrumentname\":\"TAL-2M\",\"instrumentdiameter\":\"150\",\"instrumentsort\":\"10150 mm TAL-2M\"},{\"observationid\":\"1010\",\"objectname\":\"M 101\",\"observationdate\":\"20040713\",\"observationdescription\":\"niet al te moeilijk te vinden, maar toch een behoorlijk zwak object, zichtbaar in de zoeker, persoonlijk vind ik het een heel mooi stelsel.\",\"observerid\":\"Ward De Jonghe\",\"observername\":\"Ward De Jonghe\",\"observersortname\":\"De Jonghe Ward\",\"objectconstellation\":\"UMA\",\"objecttype\":\"GALXY\",\"objectmagnitude\":\"7.4\",\"objectsurfacebrigthness\":\"14.4\",\"instrumentid\":\"1072\",\"instrumentname\":\"Sky-watcher 20cm\",\"instrumentdiameter\":\"200\",\"instrumentsort\":\"10200 mm Sky-watcher 20cm\"}]";
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		deepskyFragmentView=inflater.inflate(R.layout.deepskyfragment, container, false);
		text1_textview=((TextView)deepskyFragmentView.findViewById(R.id.deepskyfragment_text1_textview_id));
		text2_textview=((TextView)deepskyFragmentView.findViewById(R.id.deepskyfragment_text2_textview_id));
 		if(savedInstanceState==null) {
	    }
		else {
			savedState=savedInstanceState.getBundle("savedState");
		}
 		if(savedState!=null) {
	    	//text1_textview.setText(savedState.getString("text1_textview"));
 		}
 		savedState=null;
 		text1_textview.setText("Test line 1\nTest Line 2");
 		text2_textview.setText("Observations come here");
 		//GetDslCommand.getCommand("maxobservationid", "", new GetDslCommandOnResult() { @Override public void onResultAvailable(String result) { getObservationsFromMaxId(result); } });
 		displayObservations("");
 		return deepskyFragmentView;
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    super.onSaveInstanceState(savedInstanceState);
	    savedInstanceState.putBundle("savedState", saveState());
	}
    private Bundle saveState() {
        Bundle state = new Bundle();
        //state.putString("text1_textview", text1_textview.getText().toString());
        return state;
    }
    
    private void getObservationsFromMaxId(String result) {
    	GetDslCommand.getCommand("observationsfromto", "&from="+((Integer)(Integer.getInteger(result)-10)).toString()+"&to="+result, new GetDslCommandOnResult() { @Override public void onResultAvailable(String result) { displayObservations(result); } });    	
    }
    
    private void displayObservations(String result){
    	try {
    		JSONArray jsonArray = new JSONArray(test);
    	    for(int i=0; i<jsonArray.length();i++) {
    	    	JSONObject jsonObject=jsonArray.getJSONObject(i);
    	    	text2_textview.setText(text2_textview.getText()+"\n");
    	    	text2_textview.setText(text2_textview.getText()+" "+jsonObject.getString("observationdate"));
    	    	text2_textview.setText(text2_textview.getText()+" "+jsonObject.getString("objectname"));
    	    	text2_textview.setText(text2_textview.getText()+" "+jsonObject.getString("observername"));
    	    }
        } catch (Exception e) {
            Toast.makeText(MainActivity.mainActivity, "DeepskyFragment Exception 1", Toast.LENGTH_LONG).show();
        }

    }

}
