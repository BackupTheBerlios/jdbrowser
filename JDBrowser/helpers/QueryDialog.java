package helpers;

import org.gnu.gtk.Dialog;
import org.gnu.gtk.GtkStockItem;
import org.gnu.gtk.HBox;
import org.gnu.gtk.Label;
import org.gnu.gtk.PolicyType;
import org.gnu.gtk.ScrolledWindow;
import org.gnu.gtk.event.DialogEvent;
import org.gnu.gtk.event.DialogListener;

public class QueryDialog extends Dialog{

        private String title = ""; 
        private String message = "";

        private boolean result = false;
        
        public QueryDialog(String message) {
                super();
                this.title = "";
            this.message = message;
                doImplementation();
        }

        
        private void doImplementation()
        {
                
                this.addButton(GtkStockItem.YES, 1);
                this.addButton(GtkStockItem.NO, 2);
                this.setTitle(this.title);
                this.setDefaultSize(400,200);
                HBox mainBox = new HBox(false,0);
                this.getDialogLayout().add(mainBox);
                
                ScrolledWindow sWindow = new ScrolledWindow(null,null);
                sWindow.setBorderWidth(10);
                sWindow.setPolicy(PolicyType.AUTOMATIC,PolicyType.AUTOMATIC);
                
                Label warnLabel = new Label(this.message);
                sWindow.addWithViewport(warnLabel);
                
                mainBox.packStart(sWindow,true, true, 0);

                this.addListener(new DialogListener(){
                        public boolean dialogEvent(DialogEvent event) {
                                if(event.getResponse() == 1){
                                        result = true;
                                }else{
                                        result = false;
                                }
                                hideAll();
                                return false;
                        }
                });
                
        }
        
        public boolean getAnswer(){
                return this.result;
        }
}
