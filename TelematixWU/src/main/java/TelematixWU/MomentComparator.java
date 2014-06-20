package TelematixWU;

/**
 * Created with IntelliJ IDEA.
 * User: wedge
 * Date: 6/20/14
 * Time: 2:20 PM
 * To change this template use File | Settings | File Templates.
 */
import java.util.Comparator;

public class MomentComparator implements Comparator<Moment>{

    @Override
    public int compare(Moment moment1, Moment moment2) {
        if(moment1.getDatetime().getMillis() > moment2.getDatetime().getMillis()){
            return 1;
        } else {
            return -1;
        }
    }
}

