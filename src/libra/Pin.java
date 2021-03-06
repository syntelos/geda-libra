package libra;

import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;

/**
 * A pin as used by {@link Symbol}.
 * 
 * <h3>Implementation notes</h3>
 * 
 * Pin extends rectangle for some computational convenience in layout
 * programming.  It employs this (the super) rectangle as a line, like
 * the pin attribute type.
 * 
 * 
 * @author John Pritchard <jdp@ulsf.net>
 */
public class Pin
    extends Attribute
    implements Comparable<Pin>
{
    public enum Type {
        in, out, io, oc, oe, pas, tp, tri, clk, pwr;

        public final static Type For(String type){
            if (null != type){
                try {
                    return Type.valueOf(type.toLowerCase());
                }
                catch (RuntimeException exc){

                    if ("I".equalsIgnoreCase(type))
                        return Type.in;
                    else if ("O".equalsIgnoreCase(type))
                        return Type.out;
                    else if ("I/O".equalsIgnoreCase(type))
                        return Type.io;
                    else if ("POWER".equalsIgnoreCase(type))
                        return Type.pwr;
                    else
                        return Type.pas;
                }
            }
            else
                return null;
        }

        public final static Type[] Add(Type[] list, Type item){
            if (null == item)
                return list;
            else if (null == list)
                return new Type[]{item};
            else {
                int len = list.length;
                Type[] copier = new Type[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = item;
                return copier;
            }
        }
    }

    public int number, sequence;

    public boolean inverted;

    protected String[] namein;

    protected Type[] typein;

    public String style = "line", net = "";

    protected Layout.Position posit;

    protected String name;

    protected Type type;

    protected Label label;


    public Pin(int number, boolean inverted){
        super(Attribute.Type.P);
        this.number = number;
        this.sequence = (this.number - 1);
        this.inverted = inverted;
    }
    public Pin(String[] line){
        super(Attribute.Type.P);
        this.number = Integer.parseInt(line[0]);
        this.sequence = (this.number - 1);

        final String name = line[1];

        if (name.startsWith("\\_") && name.endsWith("\\_")){
            this.inverted = true;
            this.namein = new String[]{
                name
            };
        }
        else {
            switch(name.charAt(0)){
            case '~':
            case '!':
                this.inverted = true;
                this.namein = new String[]{
                    "\\_"+name.substring(1)+"\\_"
                };
                break;
            default:
                this.inverted = false;
                this.namein = new String[]{
                    name
                };
                break;
            }
        }

        this.typein = new Type[]{
            Type.For(line[2])
        };
    }
    public Pin(){
        super(Attribute.Type.P);
    }


    public boolean add(String[] line){
        int pin = Integer.parseInt(line[0]);
        if (this.number == pin){

            if (1 < line.length){

                this.namein = Add(this.namein,new StringTokenizer(line[1]," \t"));

                if (2 < line.length){

                    this.typein = Type.Add(this.typein,Type.For(line[2]));
                }
            }

            return true;
        }
        else
            return false;
    }
    public int countName(){
        if (null == this.namein)
            return 0;
        else
            return this.namein.length;
    }
    public String getName(int idx){
        if (-1 < idx && idx < this.countName())
            return this.namein[idx];
        else
            return null;
    }
    public String getName(){
        if (null == this.name){
            StringBuilder strbuf = new StringBuilder();
            for (String name: this.namein){
                if (0 < strbuf.length())
                    strbuf.append('/');
                strbuf.append(name);
            }
            this.name = strbuf.toString();
        }
        return this.name;
    }
    public String nameString(char sep){
        StringBuilder strbuf = new StringBuilder();
        for (String name: this.namein){
            if (0 < strbuf.length())
                strbuf.append(sep);
            strbuf.append(name);
        }
        return strbuf.toString();
    }
    public int countType(){
        if (null == this.typein)
            return 0;
        else
            return this.typein.length;
    }
    public Pin.Type getType(int idx){
        if (-1 < idx && idx < this.countType())
            return this.typein[idx];
        else
            return null;
    }
    public Pin.Type getType(){
        if (null == this.type){
            if (1 == this.typein.length)
                return (this.type = this.typein[0]);
            else {
                int io = 0;
                for (Type type: this.typein){

                    switch (type){
                    case in:
                        io |= 1;
                        break;
                    case out:
                        io |= 2;
                        break;

                    default:
                        return (this.type = Type.io);
                    }
                }
                switch (io){
                case 1:
                    return (this.type = Type.in);
                case 2:
                    return (this.type = Type.out);
                case 3:
                    return (this.type = Type.io);
                }
            }
        }
        return this.type;
    }
    public boolean isPassive(){
        return (Type.pas == this.getType());
    }
    public boolean isNotPassive(){
        return (Type.pas != this.getType());
    }
    public Layout.Position getPosition(Layout layout){
        if (null == layout)
            return this.getPosition();
        else {
            if (null == this.posit)
                this.posit = layout.getPosition(this);
            return this.posit;
        }
    }
    public Layout.Position getPosition(){
        if (null == this.posit){
            switch(this.whichend){
            case 0:
                if (this.x1 == this.x2){

                    if (this.y1 > this.y2)
                        return Layout.Position.B;
                    else if (this.y1 == this.y2)
                        throw new IllegalStateException("Bad pin coordinates not line.");
                    else
                        return Layout.Position.T;
                }
                else if (this.y1 == this.y2){

                    if (this.x1 > this.x2)
                        return Layout.Position.L;
                    else if (this.x1 == this.x2)
                        throw new IllegalStateException("Bad pin coordinates not line.");
                    else
                        return Layout.Position.R;
                }
                else
                    throw new IllegalStateException("Bad pin coordinates not horizontal or vertical.");

            case 1:
                if (this.x1 == this.x2){

                    if (this.y1 > this.y2)
                        return Layout.Position.T;
                    else if (this.y1 == this.y2)
                        throw new IllegalStateException("Bad pin coordinates not line.");
                    else
                        return Layout.Position.B;
                }
                else if (this.y1 == this.y2){

                    if (this.x1 > this.x2)
                        return Layout.Position.R;
                    else if (this.x1 == this.x2)
                        throw new IllegalStateException("Bad pin coordinates not line.");
                    else
                        return Layout.Position.L;
                }
                else
                    throw new IllegalStateException("Bad pin coordinates not horizontal or vertical.");

            default:
                throw new IllegalStateException(String.format("Bad value for pin.whichend (%d)",this.whichend));
            }
        }
        else
            return this.posit;
    }
    public Attribute getLabel(){

        for (Attribute at: this){
            if (at.isPinLabel())
                return at;
        }
        return null;
    }
    public int layout0(){

        return Layout.Dimension.Label(this.getName());
    }
    public void layout1(Layout.Dimension b){

        if (null != this.posit){

            switch(this.posit){
            case T:
                /*
                 *    [0]
                 *     |
                 *  --[1]--
                 */
                this.xyxy( (b.x),                      (b.y+Layout.Dimension.Y0),  (b.x),   (b.y));
                break;
            case L:
                /*
                 *       |
                 * [0]--[1]
                 *       |
                 */
                this.xyxy( (0),                        (b.y),                      (b.x),   (b.y));

                break;
            case B:
                /*
                 *  --[1]--
                 *     |
                 *    [0]  
                 */
                this.xyxy( (b.x),                      (0),                        (b.x),   (b.y));

                break;
            case R:
                /*
                 *   |
                 *  [1]--[0]
                 *   |     
                 */
                this.xyxy( (b.x+Layout.Dimension.X0),  (b.y),                      (b.x),   (b.y));

                break;
            }

            this.clear();

            final Attribute pin = this.pin();
            {
                this.add(Attribute.Type.T).text("pinseq",this.sequence).loc(pin);
                this.add(Attribute.Type.T).text("pintype",this.getType()).loc(pin);
            }
            switch(this.posit){
            case T:
                this.add(Attribute.Type.T).text("pinnumber",this.number,Attribute.Show.Value).rel(pin,-50,-205).aa(90,0);
                this.add(Attribute.Type.T).text("pinlabel",this.getName(),Attribute.Show.Value).rel(pin,0,-355).aa(90,6).layoutText();
                break;
            case L:
                this.add(Attribute.Type.T).text("pinnumber",this.number,Attribute.Show.Value).rel(pin,205,45).aa(0,6);
                this.add(Attribute.Type.T).text("pinlabel",this.getName(),Attribute.Show.Value).rel(pin,355,-5).aa(0,0).layoutText();
                break;
            case B:
                this.add(Attribute.Type.T).text("pinnumber",this.number,Attribute.Show.Value).bot(pin,-50,205).aa(90,6);
                this.add(Attribute.Type.T).text("pinlabel",this.getName(),Attribute.Show.Value).bot(pin,0,355).aa(90,0).layoutText();
                break;
            case R:
                this.add(Attribute.Type.T).text("pinnumber",this.number,Attribute.Show.Value).rel(pin,-205,45).aa(0,0);
                this.add(Attribute.Type.T).text("pinlabel",this.getName(),Attribute.Show.Value).rel(pin,-355,-5).aa(0,6).layoutText();
                break;
            }
            if (this.inverted)
                this.add(this.logicbubble(this.posit));
        }
        else
            throw new IllegalStateException("Missing layout position");
    }
    public int compareTo(Pin that){
        if (this.number == that.number)
            return 0;
        else if (this.number < that.number)
            return -1;
        else
            return 1;
    }

    public final static String[] Add(String[] list, String item){
        if (null == item)
            return list;
        else if (null == list)
            return new String[]{item};
        else {
            int len = list.length;
            String[] copier = new String[len+1];
            System.arraycopy(list,0,copier,0,len);
            copier[len] = item;
            return copier;
        }
    }
    public final static String[] Add(String[] list, StringTokenizer item){
        if (null == item)
            return list;
        else if (null == list){
            final int count = item.countTokens();
            if (1 > count)
                return null;
            else {
                list = new String[count];
                for (int cc = 0; cc < count; cc++){
                    list[cc] = item.nextToken();
                }
                return list;
            }
        }
        else {
            final int len = list.length;
            final int count = item.countTokens();

            String[] copier = new String[len+count];
            System.arraycopy(list,0,copier,0,len);

            for (int cc = 0; cc < count; cc++){
                copier[len+cc] = item.nextToken();
            }
            return copier;
        }
    }
}
