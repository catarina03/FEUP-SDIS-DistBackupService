class Request{
    public String operation;
    public String name;
    public String ip_address;

    public Request(){};
    
    public Request(String op, String n, String ip){
        this.operation = op;
        this.name=n;
        this.ip_address=ip;
    }

    public Request(String op, String n) {
        this.operation = op;
        this.name = n;
    }
}