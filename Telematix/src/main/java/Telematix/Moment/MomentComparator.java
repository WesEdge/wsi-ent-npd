package Telematix.Moment;

/**
 * Created with IntelliJ IDEA.
 * User: wedge
 * Date: 5/23/14
 * Time: 5:53 PM
 * To change this template use File | Settings | File Templates.
 */

import java.util.Comparator;

public class MomentComparator implements Comparator<MomentInterface>{

    @Override
    public int compare(MomentInterface moment1, MomentInterface moment2) {
        if(moment1.getDatetime().getMillis() > moment2.getDatetime().getMillis()){
            return 1;
        } else {
            return -1;
        }
    }
}
