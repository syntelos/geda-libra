package libra;

import java.awt.geom.Path2D;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * SVG path expression tool ensures gEDA/GSCHEM friendly format.
 * 
 * @author John Pritchard <jdp@ulsf.net>
 */
public class Path
    extends Object
    implements Iterable<Path.Code>
{
    public enum Token {
	Unknown, Coordinate, M, m, Z, z, L, l, H, h, V, v, C, c, S, s, Q, q, T, t, A, a;
    }

    public static class Code {

	public final Path.Token type;

	public final double[] coords;

	public Code(Path.Token type, double... coords){
	    super();
	    this.type = type;
	    this.coords = coords;
	}

	public String toString(){
	    StringBuilder strbuf = new StringBuilder();
	    strbuf.append(this.type.name());

	    for (double d: this.coords){
		strbuf.append(' ');
		strbuf.append(String.format("%5.1f",d));
	    }
	    return strbuf.toString();
	}

	public final static Code[] Add(Code[] list, Code item){
	    if (null == item)
		return list;
	    else if (null == list)
		return new Code[]{item};
	    else {
		final int len = list.length;
		Code[] copier = new Code[len+1];
		System.arraycopy(list,0,copier,0,len);
		copier[len] = item;
		return copier;
	    }
	}

	public static class Iterator
	    extends Object
	    implements java.util.Iterator<Code>
	{

	    private final Code[] list;
	    private final int length;
	    private int index;

	    public Iterator(Code[] list){
		super();
		if (null == list){
		    this.list = null;
		    this.length = 0;
		}
		else {
		    this.list = list;
		    this.length = list.length;
		}
	    }

	    public boolean hasNext(){
		return (this.index < this.length);
	    }
	    public Code next(){
		return (this.list[this.index++]);
	    }
	    public void remove(){
		throw new UnsupportedOperationException();
	    }
	}
    }

    public final Code[] listing;


    public Path(String pexp){
	super();

	Code[] list = null;

	Parser p = new Parser(pexp);

        Path.Token state = null;
        double mx = 0, my = 0, sx = 0, sy = 0;

	Double coord0 = null, coord1 = null, coord2 = null, 
	    coord3 = null, coord4 = null, coord5 = null;

        for (Path.Token tok : p){

            switch(tok){
            case Coordinate:
		if (null == coord0){
		    coord0 = p.getCoordinate();
		    if (null != state){
			switch(state){

			case H:
			    sx = coord0;
			    coord0 = null;
			    list = Code.Add(list,new Code(Path.Token.L,sx,sy));
			    break;
			case h:
			    sx += coord0;
			    coord0 = null;
			    list = Code.Add(list,new Code(Path.Token.L,sx,sy));
			    break;
			case V:
			    sy = coord0;
			    coord0 = null;
			    list = Code.Add(list,new Code(Path.Token.L,sx,sy));
			    break;
			case v:
			    sy += coord0;
			    coord0 = null;
			    list = Code.Add(list,new Code(Path.Token.L,sx,sy));
			    break;
			default:
			    break;
			}
		    }
		    else
			throw new Error("C0");
		}
		else if (null == coord1){
		    coord1 = p.getCoordinate();

		    if (null != state){
			switch(state){

			case M:
			    list = Code.Add(list,new Code(Path.Token.M,(mx = coord1),(my = coord1)));
			    coord0 = null;
			    coord1 = null;
			    sx = mx;
			    sy = my;
			    break;
			case m:
			    list = Code.Add(list,new Code(Path.Token.M,(mx += coord1),(my += coord1)));
			    coord0 = null;
			    coord1 = null;
			    sx = mx;
			    sy = my;
			    break;

			case L:
			    list = Code.Add(list,new Code(Path.Token.L,(sx = coord0),(sy = coord1)));
			    coord0 = null;
			    coord1 = null;
			    break;
			case l:
			    list = Code.Add(list,new Code(Path.Token.L,(sx += coord0),(sy += coord1)));
			    coord0 = null;
			    coord1 = null;
			    break;
			default:
			    break;
			}
		    }
		    else
			throw new Error("C1");
		}
		else if (null == coord2)
		    coord2 = p.getCoordinate();
		else if (null == coord3){
		    coord3 = p.getCoordinate();
		    if (null != state){
			switch(state){
			case Q:
			    list = Code.Add(list,new Code(Path.Token.Q,coord0,coord1,
									  (sx = coord2),(sy = coord3)));
			    coord0 = null;
			    coord1 = null;
			    coord2 = null;
			    coord3 = null;
			    break;
			case q:
			    list = Code.Add(list,new Code(Path.Token.Q,(sx + coord0),(sy + coord1),
							  (sx += coord2),(sy += coord3)));
			    coord0 = null;
			    coord1 = null;
			    coord2 = null;
			    coord3 = null;
			    break;
			default:
			    break;
			}
		    }
		    else
			throw new Error("C3");
		}
		else if (null == coord4)
		    coord4 = p.getCoordinate();
		else if (null == coord5){
		    coord5 = p.getCoordinate();
		    if (null != state){
			switch(state){
			case C:
			    list = Code.Add(list,new Code(Path.Token.C,coord0,coord1,
							  coord2,coord3,
							  (sx = coord4),(sy = coord5)));
			    coord0 = null;
			    coord1 = null;
			    coord2 = null;
			    coord3 = null;
			    coord4 = null;
			    coord5 = null;
			    break;
			case c:
			    list = Code.Add(list,new Code(Path.Token.C,(sx + coord0),(sy + coord1),
							  (sx + coord2),(sy + coord3),
							  (sx += coord4),(sy += coord5)));
			    coord0 = null;
			    coord1 = null;
			    coord2 = null;
			    coord3 = null;
			    coord4 = null;
			    coord5 = null;
			    break;
			default:
			    break;
			}
		    }
		    else
			throw new Error("C5");
		}
		else
		    throw new Error("C6");
		break;
            case M:
		list = Code.Add(list,new Code(Path.Token.M,(mx = p.getCoordinate()),(my = p.getCoordinate())));
                sx = mx;
                sy = my;
		state = tok;
                break;
            case m:
                list = Code.Add(list,new Code(Path.Token.M,(mx += p.getCoordinate()),(my += p.getCoordinate())));
                sx = mx;
                sy = my;
		state = tok;
                break;
            case Z:
            case z:
		list = Code.Add(list,new Code(tok));
		state = tok;
                break;
            case L:
                list = Code.Add(list,new Code(Path.Token.L,(sx = p.getCoordinate()),(sy = p.getCoordinate())));
		state = tok;
                break;
            case l:
                list = Code.Add(list,new Code(Path.Token.L,(sx += p.getCoordinate()),(sy += p.getCoordinate())));
		state = tok;
                break;
            case H:
                sx = p.getCoordinate();
                list = Code.Add(list,new Code(Path.Token.L,sx,sy));
		state = tok;
                break;
            case h:
                sx += p.getCoordinate();
                list = Code.Add(list,new Code(Path.Token.L,sx,sy));
		state = tok;
                break;
            case V:
                sy = p.getCoordinate();
                list = Code.Add(list,new Code(Path.Token.L,sx,sy));
		state = tok;
                break;
            case v:
                sy += p.getCoordinate();
                list = Code.Add(list,new Code(Path.Token.L,sx,sy));
		state = tok;
                break;
            case C:
                list = Code.Add(list,new Code(Path.Token.C,p.getCoordinate(),p.getCoordinate(),
					      p.getCoordinate(),p.getCoordinate(),
					      (sx = p.getCoordinate()),(sy = p.getCoordinate())));
		state = tok;
                break;
            case c:
                list = Code.Add(list,new Code(Path.Token.C,(sx + p.getCoordinate()),(sy + p.getCoordinate()),
					      (sx + p.getCoordinate()),(sy + p.getCoordinate()),
					      (sx += p.getCoordinate()),(sy += p.getCoordinate())));
		state = tok;
                break;
            case S:
            case s:
                throw new UnsupportedOperationException(tok.name());
            case Q:
                list = Code.Add(list,new Code(Path.Token.Q,p.getCoordinate(),p.getCoordinate(),
					      (sx = p.getCoordinate()),(sy = p.getCoordinate())));
		state = tok;
                break;
            case q:
                list = Code.Add(list,new Code(Path.Token.Q,(sx + p.getCoordinate()),(sy + p.getCoordinate()),
					      (sx += p.getCoordinate()),(sy += p.getCoordinate())));
		state = tok;
                break;
            case T:
            case t:
                throw new UnsupportedOperationException(tok.name());
            case A:
            case a:
                throw new UnsupportedOperationException(tok.name());
            default:
                throw new IllegalArgumentException(tok.name());
            }
        }
	this.listing = list;
    }


    public final Path2D apply(Path2D shape){

        double mx = 0, my = 0, sx = 0, sy = 0;

        for (Code c: this){

	    switch(c.type){

	    case M:
		shape.moveTo((mx = c.coords[0]),(my = c.coords[1]));
		sx = mx;
		sy = my;
		break;
	    case m:
		shape.moveTo((mx += c.coords[0]),(my += c.coords[1]));
		sx = mx;
		sy = my;
		break;
	    case Z:
	    case z:
		shape.closePath();
		break;
	    case L:
		shape.lineTo((sx = c.coords[0]),(sy = c.coords[1]));
		break;
	    case l:
		shape.lineTo((sx += c.coords[0]),(sy += c.coords[1]));
		break;
	    case H:
		sx = c.coords[0];
		shape.lineTo(sx,sy);
		break;
	    case h:
		sx += c.coords[0];
		shape.lineTo(sx,sy);
		break;
	    case V:
		sy = c.coords[0];
		shape.lineTo(sx,sy);
		break;
	    case v:
		sy += c.coords[0];
		shape.lineTo(sx,sy);
		break;
	    case C:
		shape.curveTo(c.coords[0],c.coords[1],
			      c.coords[2],c.coords[3],
			      (sx = c.coords[4]),(sy = c.coords[5]));
		break;
	    case c:
		shape.curveTo((sx + c.coords[0]),(sy + c.coords[1]),
			      (sx + c.coords[2]),(sy + c.coords[3]),
			      (sx += c.coords[4]),(sy += c.coords[5]));
		break;
	    case S:
	    case s:
		throw new UnsupportedOperationException(c.type.name());
	    case Q:
		shape.quadTo(c.coords[0],c.coords[1],
			     (sx = c.coords[2]),(sy = c.coords[3]));
		break;
	    case q:
		shape.quadTo((sx + c.coords[0]),(sy + c.coords[1]),
			     (sx += c.coords[2]),(sy += c.coords[3]));
		break;
	    case T:
	    case t:
		throw new UnsupportedOperationException(c.type.name());
	    case A:
	    case a:
		throw new UnsupportedOperationException(c.type.name());
	    default:
		throw new IllegalArgumentException(c.type.name());
	    }
        }
        return shape;
    }
    public java.util.Iterator<Path.Code> iterator(){
	return new Code.Iterator(this.listing);
    }
    public String toString(){
	StringBuilder strbuf = new StringBuilder();

	for (Code c: this){
	    if (0 < strbuf.length())
		strbuf.append(' ');

	    strbuf.append(c);
	}
	return strbuf.toString();
    }


    /**
     * Parse SVG Path "d" attribute value expression.
     */
    public final static class Parser
        extends Object
        implements Iterable<Path.Token>,
                   Iterator<Path.Token>
    {

        private final char[] string;
        private int index;
        private java.lang.Double coordinate;


        public Parser(String string){
            super();
            if (null != string){
                this.string = string.trim().toCharArray();
                if (0 == this.string.length)
                    throw new IllegalArgumentException();
            }
            else
                throw new IllegalArgumentException();
        }


        public java.lang.Double getCoordinate(){
            java.lang.Double c = this.coordinate;
            if (null != this.coordinate){
                this.coordinate = null;
                return c;
            }
            else if (this.hasNext() && Token.Coordinate == this.next()){
                c = this.coordinate;
                this.coordinate = null;
                return c;
            }
            else
                throw new java.util.NoSuchElementException();
        }
        public boolean hasNext(){
            return (this.index < this.string.length);
        }
        public Path.Token next(){
            this.coordinate = null;
            if (this.index < this.string.length){
                Path.Token token = null;
                int start = this.index;
                int end = start;
                boolean decpt = false;
                scan:
                while (this.index < this.string.length){

                    switch(this.string[this.index]){
                    case ' ':
                    case ',':
                        if (this.index != start){
                            end = (this.index-1);
                            this.index++;
                            break scan;
                        }
                        break;
                    case '.':
                        if (null != token){
                            if (decpt || Path.Token.Coordinate != token){
                                end = (this.index-1);
                                break scan;
                            }
                        }
                        else
                            token = Path.Token.Coordinate;

                        decpt = true;
                        break;
                    case '-':
                        if (null != token){
                            end = (this.index-1);
                            break scan;
                        }
                        else
                            token = Path.Token.Coordinate;
                        break;
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        if (null == token)
                            token = Path.Token.Coordinate;
                        else if (token != Path.Token.Coordinate){
                            end = (this.index-1);
                            break scan;
                        }
                        break;
                    default:
                        if (null == token)
                            return Path.Token.valueOf(String.valueOf(this.string[this.index++]));
                        else {
                            end = (this.index-1);
                            break scan;
                        }
                    }
                    this.index++;
                }

                if (Path.Token.Coordinate == token){

                    if (start == end && this.index == this.string.length)
                        end = (this.index-1);

                    this.coordinate = java.lang.Double.parseDouble(new String(this.string,start,(end-start+1)));
                    return token;
                }
                else
                    return Path.Token.Unknown;
            }
            else
                throw new java.util.NoSuchElementException();
        }
        public void remove(){
            throw new UnsupportedOperationException();
        }
        public Iterator<Path.Token> iterator(){
            return this;
        }
    }

}
