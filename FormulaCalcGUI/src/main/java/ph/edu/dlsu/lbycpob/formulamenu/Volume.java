package ph.edu.dlsu.lbycpob.formulamenu;

public class Volume implements IFormula {
    private  final String[] parameterList = {"Length", "Width","Height","Volume"};
    private double length;
    private double width;
    private double height;
    private double volume;

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public void computeVolume() {
        volume = length * width * height;
    }

    public void computeLength() {
        length = volume / (width * height);
    }

    public void computeWidth() {
        width = volume / (length * height);
    }

    public void computeHeight() {
        height = volume / (length * width);
    }

    @Override
    public double compute(String variable, String[] values) {
        if (variable == null) {
            return 0;
        }
        if (variable.equalsIgnoreCase("Volume")) {
            length = Double.parseDouble(values[0]);
            width = Double.parseDouble(values[1]);
            height = Double.parseDouble(values[2]);
            computeVolume();
            return volume;

        } else if (variable.equalsIgnoreCase("Length")) {
            width = Double.parseDouble(values[0]);
            height = Double.parseDouble(values[1]);
            volume = Double.parseDouble(values[2]);
            computeLength();
            return length;

        } else if (variable.equalsIgnoreCase("Width")) {
            height = Double.parseDouble(values[0]);
            volume = Double.parseDouble(values[1]);
            length = Double.parseDouble(values[2]);
            computeWidth();
            return width;

        } else if (variable.equalsIgnoreCase("Height")) {
            volume = Double.parseDouble(values[0]);
            length = Double.parseDouble(values[1]);
            width = Double.parseDouble(values[2]);
            computeHeight();
            return height;
        }
        return 0;
    }

    @Override
    public String[] getParameterList() {
        return parameterList;
    }
}
