package gsn.utils.models;

import gsn.beans.DataTypes;
import gsn.beans.StreamElement;

/**
 * This class is just an example of implementation of an AsbtractModel.
 * It always returns the last element and if no one is set, it builds one with the default value.
 * The default value can be defined as a parameter.
 * @author jeberle
 *
 */
public class DummyModel extends AbstractModel {
	
	private StreamElement lastone;
	private int defaultValue = 0;

	@Override
	public StreamElement pushData(StreamElement streamElement) {
		lastone = streamElement;
		return lastone;
	}

	@Override
	public StreamElement[] query(StreamElement params) {
		
		if (lastone != null){
			return new StreamElement[] {lastone};
		}
		else{
			return new StreamElement[]{new StreamElement(new String[]{"value"},
					new Byte[]{DataTypes.INTEGER}, new Integer[]{defaultValue})};
		}
	}

	@Override
	public void setParam(String k, String string) {
		if (k.equalsIgnoreCase("default")){
			try{
				defaultValue = Integer.parseInt(string);
			}
			catch (NumberFormatException e) {
			}
		}

	}

}
