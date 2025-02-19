import java.util.Scanner;

class NumerologyCalculator
{
    public int calculate(int day)
    {
        int ReducedNum= 0;
        if(day > 9)
        {
            ReducedNum = (day% 10) + (day / 10);
            if(ReducedNum > 9)
            {
                ReducedNum = (ReducedNum % 10) + (ReducedNum / 10);
                if(ReducedNum > 9)
            {
                ReducedNum = (ReducedNum % 10) + (ReducedNum / 10);
            }
            }       
        }
        else
        {
         ReducedNum = day; 
        }

        return ReducedNum;
    }

    public int NameCalc(String name)
    {
        int NameNum=0;
        String Name=name.toLowerCase();
        Name = Name.replace(" ", ""); // Remove spaces
        for(int i=0; i<Name.length(); i++)
        {
            switch (Name.charAt(i)) {
                case 'a':
                case 'i':
                case 'j':
                case 'q':
                case 'y':
                    NameNum += 1;
                    break;
                case 'b':
                case 'c':
                case 'k':
                case 'r':
                    NameNum += 2;
                    break;
                case 'g':
                case 'l':
                case 's':
                    NameNum += 3;  
                    break;
                case 'd':
                case 'm':
                case 't':
                    NameNum += 4;
                    break;
                case 'e':
                case 'n':
                    NameNum += 5;
                    break;
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                    NameNum += 6;
                    break;
                case 'o':
                case 'z':
                    NameNum += 7;
                    break;
                case 'f':
                case 'h':
                case 'p':
                    NameNum += 8;
                    break;
                case ' ':
                    NameNum += 0;
                    break;
                default:
                    break; 

            }
    }
    NameNum = calculate(NameNum);

    return NameNum;
    }

    public static void main(String[] args)
    {
        Scanner scan = new Scanner(System.in);
        
        String boldGrayLine = "123456789 NUMEROLOGY CALCULATOR 987654321";
        System.out.println("\t\t\t\t\t\t"+ boldGrayLine);

        System.out.println("Enter the day of birth: ");
        int day = scan.nextInt();
        System.out.println("Enter the month of birth: ");
        int month = scan.nextInt();
        System.out.println("Enter the year of birth: ");
        int year = scan.nextInt();
        System.out.println("Enter First name: ");
        String fname = scan.next();
        System.out.println("Enter Last name: ");
        String Lname = scan.next();

        

        NumerologyCalculator calculator = new NumerologyCalculator();
        System.out.print("Psychic Number: " + calculator.calculate(day)+ "\t\t");

        int DestinyNum = calculator.calculate(day) + calculator.calculate(month) + calculator.calculate(year);
        System.out.print("Destiny Number: " + calculator.calculate(DestinyNum)+ "\t\t");

        NumerologyCalculator nameCalc = new NumerologyCalculator();
        int NameNumber= nameCalc.NameCalc(fname) + nameCalc.NameCalc(Lname);
        System.out.println("Name Number: " +NameNumber);

        scan.close();
    }
}