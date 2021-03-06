import java.util.*;

class Cor {
    double x;
    double y;

    Cor(double X, double Y) {
        x = X;
        y = Y;
    }
}

class Option implements Comparable<Option> {
    int angle, player_id, distance,angle_diff;
    double annoys;
    int score;
    Cor des;

    Option(int angle, int player_id) {
        this.angle = angle;
        this.player_id = player_id;
    }

    @Override
    public int compareTo(Option t1) {
        return this.score < t1.score ? 1 : this.score == t1.score ? (this.distance < t1.distance ? -1 : 1) : -1;
    }
}

class Strategy {
    private static int goal_period;
    private static Random rnd = new Random();

    static Player[] init_players() {
        Player[] players = new Player[5];
        players[0] = new Player("0", new Position(-6.3, 1.3));
        players[1] = new Player("1", new Position(-6.1, 0));
        players[2] = new Player("2", new Position(-6.3, -1.3));
        players[3] = new Player("3", new Position(-1, 0));
        players[4] = new Player("4", new Position(-3, 2));


        return players;
    }

    private static int find_angle(Cor B, Cor A) {
        // B is the origin and A is destination
        int angle = Math.abs((int) Math.toDegrees(Math.atan((A.y - B.y) / (A.x - B.x))));
        if (A.x > B.x) {
            if (A.y < B.y)
                angle = 360 - angle;
        } else {
            if (A.y < B.y)
                angle += 180;
            else
                angle = 180 - angle;
        }
        return angle;
    }

    private static Cor find_tok(Cor A, Cor B, boolean ball) {
        double tx, ty;
        if (ball) {
            double m = (A.y - B.y) / (A.x - B.y);

            tx = A.x - Math.sqrt(0.5625 / (Math.pow(m, 2) + 1));
            double S = A.y - (m * A.x);
            ty = S + (m * tx);
        } else {
            double m = (A.y - B.y) / (A.x - B.y);
            tx = A.x - Math.sqrt(1 / (Math.pow(m, 2) + 1));
            double S = A.y - (m * A.x);
            ty = S + (m * tx);
        }
        return new Cor(tx, ty);
    }

    private static double Distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
    }

    private static double dotline(double xd, double yd, double x1, double y1, double x2, double y2) {
        double a, b, c;
        a = y2 - y1;
        b = x2 - x1;
        c = x1 * y2 - x2 * y1;
        return Math.abs(a * xd + b * yd + c) / Math.sqrt(a * a + b * b);
    }

    private static Cor find_Shadow_for_ball(Cor Ball_Cor, Cor Player_Cor, Cor Gate_Cor) {
        double m = 0;
        double x = 0;
        if (Ball_Cor.y > Player_Cor.y) {
            m = (Ball_Cor.y - (7.5 - Gate_Cor.y)) / (Ball_Cor.x - Gate_Cor.x);
            x = (3.75 - (Ball_Cor.y - (m * Ball_Cor.x))) / m;
            return new Cor(x, 3.75);
        } else {
            m = (Ball_Cor.y - (-7.5 - Gate_Cor.y)) / (Ball_Cor.x - Gate_Cor.x);
            x = (-3.75 - (Ball_Cor.y - (m * Ball_Cor.x))) / m;
            return new Cor(x, -3.75);
        }
    }

    private static Cor find_Shadow_for_Player(Cor Player_Cor, Cor BallTok_Cor) {
        double m = 0;
        double x = 0;
        if (BallTok_Cor.y > 0) {
            m = (7 - BallTok_Cor.y - Player_Cor.y) / (BallTok_Cor.x - Player_Cor.x);
            x = (3.5 - (Player_Cor.y - (m * Player_Cor.x))) / m;
            return new Cor(x, 3.5);
        } else {
            m = (-7 - BallTok_Cor.y - Player_Cor.y) / (BallTok_Cor.x - Player_Cor.x);
            x = (-3.5 - (Player_Cor.y - (m * Player_Cor.x))) / m;
            return new Cor(x, -3.5);
        }
    }

    private static int is_mohafez(Cor Player_Cor, Cor Ball_Cor, Game game) {
        if (Player_Cor.y > 1.4 || Player_Cor.y < -1.4)
            return 0;
        int tedad = 0;
        for (int i = 0; i < 5; i++) {
            double px = game.getMyTeam().getPlayer(i).getPosition().getX();
            double py = game.getMyTeam().getPlayer(i).getPosition().getY();
            if (py <= 1.4 && py >= -1.4 && Distance(px,py,-7,0) <= 3 && px < Ball_Cor.x)
                tedad++;
        }
        if (tedad < 3)
            return (3 - tedad) * 4;

        return 0;
    }

    //From Start to here is Ok :)Trusted(:
    //From Start to here is Ok :)Trusted(:


    private static double find_annoy(Cor A, Cor B, boolean ball, int Player_id, Game game) {
        double annoys = 0;
        for (int i = 0; i < 5; i++) {
            if (i == Player_id) continue;
            double P_x = game.getMyTeam().getPlayer(i).getPosition().getX();
            double P_y = game.getMyTeam().getPlayer(i).getPosition().getY();
            if (ball) {
                if (dotline(P_x, P_y, A.x, A.y, B.x, B.y) < 0.75) {

                    if (Distance(A.x, A.y, B.x, B.y) >= Math.max(Distance(A.x, A.y, P_x, P_y), Distance(B.x, B.y, P_x, P_y))) {
                        annoys++;

                    }
                }
            } else {
                if (dotline(P_x, P_y, A.x, A.y, B.x, B.y) < 1) {
                    if (Distance(A.x, A.y, B.x, B.y) >= Math.max(Distance(A.x, A.y, P_x, P_y), Distance(B.x, B.y, P_x, P_y))) {
                        annoys += 1.1 - (Math.abs(0.5 - dotline(P_x, P_y, A.x, A.y, B.x, B.y)) * 2);
                    }
                }
            }

        }
        for (int i = 0; i < 5; i++) {
            double P_x = game.getOppTeam().getPlayer(i).getPosition().getX();
            double P_y = game.getOppTeam().getPlayer(i).getPosition().getY();

            if (ball) {
                if (Math.abs(dotline(P_x, P_y, A.x, A.y, B.x, B.y)) < 0.75) {
                    if (Distance(A.x, A.y, B.x, B.y) >= Math.max(Distance(A.x, A.y, P_x, P_y), Distance(B.x, B.y, P_x, P_y))) {
                        annoys++;
                    }
                }
            } else {

                if (Math.abs(dotline(P_x, P_y, A.x, A.y, B.x, B.y)) < 1) {
                    if (Distance(A.x, A.y, B.x, B.y) >= Math.max(Distance(A.x, A.y, P_x, P_y), Distance(B.x, B.y, P_x, P_y))) {
                        annoys += 1.1 - (Math.abs(0.5 - dotline(P_x, P_y, A.x, A.y, B.x, B.y)) * 2);
                    }
                }
            }
        }
//        System.out.println("annoys:"+annoys+" , id:"+ Player_id +" , Ax:"+A.x+", y:"+A.y +" , Bx:"+B.x+", y:"+B.y);

        return annoys;
    }

    private static int annoy(Cor player_Cor, Cor Ball_cor, Cor Gate_Cor) {
        int annoy = 0;

        if (dotline(Ball_cor.x, Ball_cor.y, player_Cor.x, player_Cor.y, Gate_Cor.x, Gate_Cor.y) <= 1)
            annoy++;
        return annoy;
    }

    private static int get_Score(Cor Player_Cor, Cor Ball_Cor, Cor Gate_Cor,int id,Game game) {
        int score = 0;
        if (annoy(Player_Cor, Ball_Cor, Gate_Cor) == 1) {
            // own goal
            score = -99;
        }
        score -= find_annoy(Player_Cor,Gate_Cor,false,id,game)*2;
        double distance = Distance(Player_Cor.x,Player_Cor.y,Gate_Cor.x,Gate_Cor.y);
        if (5.0<=distance&&distance<=8.0){
            score -= distance*2;
        }else if (distance>8){
            score -= distance*2;
        }else{
            score -= 30-distance*6;
        }
        return score;
    }

    private static int get_Score(Cor Player_Cor, Cor Ball_Cor, Cor Gate_Cor, int i, int Player_id, Game game, Option option) {
        int score = 0;
        if (i == 0) {
            score -= find_annoy(Player_Cor, find_tok(Ball_Cor, Gate_Cor, true), false, Player_id, game) * 8;
            score -= find_annoy(Ball_Cor, Gate_Cor, true, Player_id, game) * 4;
            option.annoys = (find_annoy(Player_Cor, find_tok(Ball_Cor, Gate_Cor, true), false, Player_id, game) +
                    find_annoy(Ball_Cor, Gate_Cor, true, Player_id, game));
            int tafazol_angle = Math.abs(find_angle(Player_Cor, Ball_Cor) - find_angle(Ball_Cor, Gate_Cor));
            if (tafazol_angle > 180)
                tafazol_angle = 360 - tafazol_angle;
            option.angle_diff = tafazol_angle;
            score += 10 - (tafazol_angle / 9);
            double player_ball_distance = Distance(Player_Cor.x, Player_Cor.y, Ball_Cor.x, Ball_Cor.y);
            player_ball_distance += Distance(Ball_Cor.x, Ball_Cor.y, Gate_Cor.x, Gate_Cor.y)/2;
            score -= (2*player_ball_distance);
            option.distance = (int) ((player_ball_distance));
        } else if (i == 1) {
            // shoots himself to wall
            Cor player_shadow = find_Shadow_for_Player(Player_Cor, find_tok(Ball_Cor, Gate_Cor, true));
            if (player_shadow.x <= -7 || player_shadow.x >= 7) {
                score -= 9999;
            }
            score -= find_annoy(Player_Cor, player_shadow, false, Player_id, game) * 8;
            score -= find_annoy(player_shadow, Ball_Cor, false, Player_id, game) * 6;
            score -= find_annoy(Ball_Cor, Gate_Cor, true, Player_id, game) * 4;
            option.annoys = (find_annoy(Player_Cor, player_shadow, false, Player_id, game) +
                    find_annoy(player_shadow, Ball_Cor, false, Player_id, game) +
                    find_annoy(Ball_Cor, Gate_Cor, true, Player_id, game));
            int tafazol_angle = Math.abs(find_angle(player_shadow, Ball_Cor) - find_angle(Ball_Cor, Gate_Cor));
            if (tafazol_angle > 180)
                tafazol_angle = 360 - tafazol_angle;
            option.angle_diff = tafazol_angle;
            score += 10 - (tafazol_angle / 9);
            double player_shadow_distance = Distance(Player_Cor.x, Player_Cor.y, Gate_Cor.x, Gate_Cor.y);
            player_shadow_distance += Distance(player_shadow.x, player_shadow.y, Gate_Cor.x, Gate_Cor.y)/2;
            score -= (2.5*player_shadow_distance);
            option.distance = (int) (player_shadow_distance);
        } else if (i == 2){
            // shoots ball to wall
            Cor ball_shadow = find_Shadow_for_ball(Ball_Cor, Player_Cor, Gate_Cor);
            if (ball_shadow.x <= -7 || ball_shadow.x >= 7) {
                score -= 9999;
            }
            score -= find_annoy(Player_Cor, Ball_Cor, false, Player_id, game) * 8;
            score -= find_annoy(Ball_Cor, ball_shadow, true, Player_id, game) * 4;
            score -= find_annoy(ball_shadow, Gate_Cor, true, Player_id, game) * 4;
            option.annoys =  (find_annoy(Player_Cor, Ball_Cor, false, Player_id, game) +
                    find_annoy(Ball_Cor, ball_shadow, true, Player_id, game) +
                    find_annoy(ball_shadow, Gate_Cor, true, Player_id, game));
            int tafazol_angle = Math.abs(find_angle(Player_Cor, Ball_Cor)
                    - find_angle(Ball_Cor, ball_shadow));
            if (tafazol_angle > 180)
                tafazol_angle = 360 - tafazol_angle;
            option.angle_diff = tafazol_angle;
            score += 10 - (tafazol_angle / 9);
            Cor mirror_gate = new Cor(Gate_Cor.x, Ball_Cor.y >0 ? 7.5 - Gate_Cor.y: -7.5 + Gate_Cor.y);
            double player_shadow_distance = Distance(Player_Cor.x, Player_Cor.y, ball_shadow.x, ball_shadow.y);
            player_shadow_distance += Distance(ball_shadow.x, ball_shadow.y, mirror_gate.x, mirror_gate.y)/2;
            score -= (2.5*player_shadow_distance);
            option.distance = (int) Math.floor(player_shadow_distance);
        }else{

            score -= find_annoy(Player_Cor, Ball_Cor, false, Player_id, game) * 8;
            score -= find_annoy(Ball_Cor, Gate_Cor, true, Player_id, game) * 4;
            option.annoys = (find_annoy(Player_Cor, find_tok(Ball_Cor, Gate_Cor, true), false, Player_id, game) +
                    find_annoy(Ball_Cor, Gate_Cor, true, Player_id, game));
//            System.out.println("O_o: " + Gate_Cor.y);
            int tafazol_angle = Math.abs(find_angle(Player_Cor, Ball_Cor) - find_angle(Ball_Cor, Gate_Cor));
            if (tafazol_angle > 180)
                tafazol_angle = 360 - tafazol_angle;
            option.angle_diff = tafazol_angle;
            score += 10 - (tafazol_angle / 9);
            double player_ball_distance = Distance(Player_Cor.x, Player_Cor.y, Ball_Cor.x, Ball_Cor.y);
            player_ball_distance += Distance(Ball_Cor.x, Ball_Cor.y, Gate_Cor.x, Gate_Cor.y)/2;
            score -= (2*player_ball_distance);
            score += Math.abs(0.2*score);
            option.distance = (int) ((player_ball_distance));
        }
//        if (score < 4)
        score -= is_mohafez(Player_Cor, Ball_Cor, game);
        return score;
    }

    private static PriorityQueue<Option> Formula(Cor Player_Cor, Cor Ball_Cor, int player_id, Game game) {
        PriorityQueue<Option> priorityQueue = new PriorityQueue<>();
        for (int j =0; j < 47; j++) {
            Cor Gate_Cor = new Cor(7, -1.15 + (j * 0.05));
            for (int i = 0; i < 4; i++) {

                if (i == 0) {
                    //direct shoot with tok
                    int angle = find_angle(Player_Cor, find_tok(Ball_Cor, Gate_Cor, true));
                    Option option = new Option(angle, player_id);
                    option.score = get_Score(Player_Cor, Ball_Cor, Gate_Cor, 0, player_id, game, option);
                    option.des = Gate_Cor;
                    if(option.score<-13)
                        option.angle=find_angle(Player_Cor, Ball_Cor);
                    priorityQueue.add(option);
                } else if (i == 1) {
                    // Player shoots himself to wall
                    Cor player_shadow = find_Shadow_for_Player(Player_Cor, find_tok(Ball_Cor, Gate_Cor, true));
                    
                    int angle = find_angle(Player_Cor, player_shadow);
                    Option option = new Option(angle, player_id);
                    option.score = get_Score(Player_Cor, Ball_Cor, Gate_Cor, 1, player_id, game, option);
                    if(player_shadow.y<-3.5 || player_shadow.y >3.5)
                        option.score-=100;
                    if(option.score<-13)
                    {
                        player_shadow = find_Shadow_for_Player(Player_Cor, Ball_Cor);
                        option.angle=find_angle(Player_Cor, player_shadow);
                    }
                    option.des = player_shadow;
                    priorityQueue.add(option);

                } else if (i==2){
                    // Player shoots ball to wall
                    Cor ball_shadow = find_Shadow_for_ball(Ball_Cor, Player_Cor, Gate_Cor);
                    int angle = find_angle(Player_Cor, find_tok(Ball_Cor, ball_shadow, true));
                    Option option = new Option(angle, player_id);
                    option.score = get_Score(Player_Cor, Ball_Cor, Gate_Cor, 2, player_id, game, option);
                    if(option.score<-13)
                    {
                        option.angle=find_angle(Player_Cor, Ball_Cor);;
                    }
                    option.des = ball_shadow;
                    priorityQueue.add(option);
                }else if(i==3)
                {
                    // Shoot with a Good boy!
                    for(int k=0;k<5;k++)
                    {
                        if(k==i)
                            continue;
                        Cor Good_Player=new Cor(game.getOppTeam().getPlayer(i).getPosition().getX(),game.getOppTeam().getPlayer(i).getPosition().getY());
                        
                        if(find_annoy(Player_Cor, Good_Player, false, player_id, game)==0)
                        {
                            Cor Maghsad = find_tok(Good_Player,find_tok(Ball_Cor,Gate_Cor,true),false);
                            int angle= find_angle(Good_Player,Maghsad);
                            Option option = new Option(angle, player_id);
                            option.score = get_Score(Good_Player, Ball_Cor, Gate_Cor, 0, player_id, game, option);
                            option.des = Maghsad;
                            int tafazol_angle = Math.abs(find_angle(Ball_Cor,Good_Player) - find_angle(Good_Player, find_tok(Ball_Cor,Gate_Cor,true)));
                            if (tafazol_angle > 180)
                                tafazol_angle = 360 - tafazol_angle;
                            option.angle_diff = tafazol_angle;
                            option.score -= (tafazol_angle / 9)+1;
                            priorityQueue.add(option);
                        }
                    }
                }
                
            }
        }
        return priorityQueue;
    }

    static Triple do_turn(Game game) {
        int C = game.getCycle();
        int myturns;
        if (C % 2 == 0)
            myturns = C / 2;
        else
            myturns = (C + 1) / 2;
        System.out.println("Cycle " + C + " start\n");
        Triple act = new Triple();
        double x2, y2;
        x2 = game.getBall().getPosition().getX();
        y2 = game.getBall().getPosition().getY();
        Cor ball_cor = new Cor(x2, y2);

        PriorityQueue<Option> priorityQueue = new PriorityQueue<>();
        for (int i = 0; i < 5; i++) {

            Cor player_cor = new Cor(game.getMyTeam().getPlayer(i).getPosition().getX(), game.getMyTeam().getPlayer(i).getPosition().getY());
            if (player_cor.x > ball_cor.x) {
                continue;
            }
            PriorityQueue<Option> tempo = Formula(player_cor, ball_cor, i, game);
            priorityQueue.add(tempo.poll());
            if (tempo.size()!=0){
                priorityQueue.add(tempo.poll());
            }
        }
//        System.out.println("Ball x:"+game.getBall().getPosition().getX()+",y:"+game.getBall().getPosition().getY());
//        for (Option option : priorityQueue) {
//            Position position = game.getMyTeam().getPlayer(option.player_id).getPosition();
//            System.out.println("id:"+option.player_id + ",x:"+position.getX()+",y:"+position.getY()+",score:" + option.score + ",angle:" + option.angle + ",distance:"
//                    + option.distance + ",annoys:" + option.annoys +
//                    ",des cor.x:" + option.des.x + ",des.cor.y:" + option.des.y+",angle_diff:"+option.angle_diff);
//        }


        Option best_option = priorityQueue.poll();
        if (best_option != null && best_option.score < -15){
            // I think it's better to move back
            PriorityQueue<Option> options = new PriorityQueue<>();
            Cor Ball_Cor = new Cor(game.getBall().getPosition().getX(), game.getBall().getPosition().getY());
            for (int i = 0; i < 5; i++) {
                Cor Player_Cor = new Cor(game.getMyTeam().getPlayer(i).getPosition().getX()
                        , game.getMyTeam().getPlayer(i).getPosition().getY());
                for (int j = 0; j < 47; j++) {
                    Cor Gate_Cor = new Cor(-7, -1.15 + (j * 0.05));
                    Option option = new Option(find_angle(Player_Cor, Gate_Cor),i);
                    option.score = get_Score(Player_Cor,Ball_Cor,Gate_Cor,i,game);
                    option.angle = find_angle(Player_Cor,Gate_Cor);
                    option.distance = (int) Distance(Player_Cor.x,Player_Cor.y,Gate_Cor.x,Gate_Cor.y);
                    priorityQueue.add(option);
                }
            }
            Option test = options.poll();
            if (test != null){
                best_option = test;
                if (game.getMyTeam().getPlayer(best_option.player_id).getPosition().getX() > 2) {
                    act.setPower(100);
                } else {
                    act.setPower((int) ((11) *
                            Distance(game.getMyTeam().getPlayer(best_option.player_id).getPosition().getX()
                                    , game.getMyTeam().getPlayer(best_option.player_id).getPosition().getY(), -7, 0)));
                }
                act.setAngle(best_option.angle);
                act.setPlayerID(best_option.player_id);
                return act;
            }
        }
        int My_Score = game.getMyTeam().getScore();
        int Opp_Score = game.getOppTeam().getScore();
        if (Opp_Score != 0){
            if (game.getCycle() - goal_period == goal_period){
                best_option = priorityQueue.poll();

            }else{
                goal_period = game.getCycle() - goal_period ;
            }
        }else{
            goal_period = game.getCycle();
        }
//        int Check_cycle = 3 + (rnd.nextInt() % 2);
//        if (My_Score < Opp_Score && myturns % Check_cycle == 0 && priorityQueue.size() >= 1 && C > 20) {
//            best_option = priorityQueue.poll();
//            if (rnd.nextInt() % 3 == 0 && priorityQueue.size() >= 1)
//                best_option = priorityQueue.poll();
//        } else if (My_Score == Opp_Score && C > 180 && priorityQueue.size() >= 1) {
//            best_option = priorityQueue.poll();
//        }
        if (best_option == null) {
            // It should be completed
            PriorityQueue<Option> optionPriorityQueue = new PriorityQueue<>();
            Cor Ball_Cor = new Cor(game.getBall().getPosition().getX(), game.getBall().getPosition().getY());
            for (int oo = 0; oo < 5; oo++) {
                Cor Player_Cor = new Cor(game.getMyTeam().getPlayer(oo).getPosition().getX(), game.getMyTeam().getPlayer(oo).getPosition().getY());
                for (int j = 0; j < 47; j++) {
                    Cor Gate_Cor = new Cor(-7, -1.15 + (j * 0.05));
                    //direct shoot
                    int angle = find_angle(Player_Cor, Gate_Cor);
                    Option option = new Option(angle, oo);
                    option.score = get_Score(Player_Cor, Ball_Cor, Gate_Cor,oo,game);
                    optionPriorityQueue.add(option);
                }
            }

            Option option = optionPriorityQueue.poll();
            //System.out.println("id:"+option.player_id+",angle:"+option.angle+",diff:"+option.angle_diff+",score:"+option.score);
            if (option.score <= -99) {
                if (option.angle != 120) {
                    act.setAngle(120);
                } else {
                    act.setAngle(220);
                }
            } else {
                act.setAngle(option.angle);
            }
            if (game.getMyTeam().getPlayer(option.player_id).getPosition().getX() > 3) {
                act.setPower(100);
            } else {
                act.setPower((int) ((11) *
                        Distance(game.getMyTeam().getPlayer(option.player_id).getPosition().getX()
                                , game.getMyTeam().getPlayer(option.player_id).getPosition().getY(), -7, 0)));
            }
            act.setPlayerID(option.player_id);

            // System.out.println("lala,"+option.player_id +",score:" + option.score + ",angle:" + option.angle + ",distance:"
            //        + option.distance + ",annoys:" + option.annoys+",des cor.x:"+option.des.x+",des.cor.y:"+option.des.y);
            //System.out.println("Cycle finished\n");
            return act;
        } else {

//            System.out.println("lala," + best_option.player_id + ",score:" + best_option.score + ",angle:" + best_option.angle + ",distance:"
//                    + best_option.distance + ",annoys:" + best_option.annoys
//                    + ",des cor.x:" + best_option.des.x + ",des.cor.y:" + best_option.des.y);
            act.setAngle(best_option.angle);
            act.setPower(100);
            act.setPlayerID(best_option.player_id);
            System.out.println("Cycle finished\n");
            return act;
        }
    }

}

// -=-=-=-=-=-=-=-=-=-=-=-=The end
