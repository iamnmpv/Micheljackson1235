import java.util.PriorityQueue;
import java.util.Random;

class Cor {
    double x;
    double y;

    Cor(double X, double Y) {
        x = X;
        y = Y;
    }
}

class Option implements Comparable<Option> {
    int angle, player_id, annoys, distance;
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
    static Random rnd = new Random();
    private static Player[] enemies = new Player[5];

    static Player[] init_players() {
        Player[] players = new Player[5];
        players[0] = new Player("R. Ahmadi", new Position(-6.5, 0.0));
        players[1] = new Player("E. Hajisafi", new Position(-5.2, 0.8));
        players[2] = new Player("M. Karimi", new Position(-5.2, -0.8));
        players[3] = new Player("M. Navidkia", new Position(-2.2, 2));
        players[4] = new Player("H. Aghili", new Position(-2, -2));

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

    private static int is_mohafez(Cor Player_Cor ,Cor Ball_Cor, Game game) {
        if (Player_Cor.y > 1.4 || Player_Cor.y < -1.4 || Player_Cor.x >= Ball_Cor.x)
            return 0;
        int tedad = 0;
        for (int i = 0; i < 5; i++) {
            double px=game.getMyTeam().getPlayer(i).getPosition().getX();
            double py=game.getMyTeam().getPlayer(i).getPosition().getY();
            if (py <= 1.4 && py >= -1.4 && px <= Ball_Cor.x)
                tedad++;
        }
        if (tedad < 3)
            return (3-tedad)*3;
        
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

//            if (Player_id == 0&&i == 4) {
//                System.out.println("O_o,1annoys:" + annoys + ",id:" + Player_id+",player_x:"+P_x+",player_y:"+P_y);
//                System.out.println("O_o,X and Y of Player 4:,name:"+game.getMyTeam().getPlayer(Player_id).getName()+",x:"+game.getMyTeam().getPlayer(4).getPosition().getX()
//                        +",y:"+game.getMyTeam().getPlayer(Player_id).getPosition().getY());
//            }
            if (ball) {
                if (Math.abs(dotline(P_x, P_y, A.x, A.y, B.x, B.y)) < 0.75) {

                    if (Distance(A.x, A.y, B.x, B.y) <= Math.max(Distance(A.x, A.y, P_x, P_y), Distance(B.x, B.y, P_x, P_y))) {
                        annoys++;
                    }
                }
            } else {
                if (Math.abs(dotline(P_x, P_y, A.x, A.y, B.x, B.y)) < 1) {
                    if (Distance(A.x, A.y, B.x, B.y) <= Math.max(Distance(A.x, A.y, P_x, P_y), Distance(B.x, B.y, P_x, P_y))) {
                        annoys+=1.1-(Math.abs(0.5 -dotline(P_x, P_y, A.x, A.y, B.x, B.y)) *2);
                    }
                }
            }
//            if (Player_id == 0&&i == 4) {
//                System.out.println("O_o,2annoys:" + annoys + ",id:" + Player_id+",player_X:"+P_x+",Player_Y:"+P_y);
//            }
        }
        for (int i = 0; i < 5; i++) {
            double P_x = game.getOppTeam().getPlayer(i).getPosition().getX();
            double P_y = game.getOppTeam().getPlayer(i).getPosition().getY();

            if (ball) {
                if (Math.abs(dotline(P_x, P_y, A.x, A.y, B.x, B.y)) < 0.75) {
                    if (Distance(A.x, A.y, B.x, B.y) <= Math.max(Distance(A.x, A.y, P_x, P_y), Distance(B.x, B.y, P_x, P_y))) {
                        annoys++;
                    }
                }else{
//                    if (Player_id == 3 &&  i ==3) {
//                        System.out.println("P_x:" + P_x + ",P_y:" + P_y + " ,and dotline:" + dotline(P_x, P_y, A.x, A.y, B.x, B.y) + ",Gate_y:" + B.y);
//
//                    }
                }
            } else {

                if (Math.abs(dotline(P_x, P_y, A.x, A.y, B.x, B.y)) < 1) {
                    if (Distance(A.x, A.y, B.x, B.y) <= Math.max(Distance(A.x, A.y, P_x, P_y), Distance(B.x, B.y, P_x, P_y))) {
                        annoys+=1.1-(Math.abs(0.5 -dotline(P_x, P_y, A.x, A.y, B.x, B.y)) *2);
                    }
                }
            }
        }
//        System.out.println("annoys:"+annoys+" , id:"+ Player_id +" , Ax:"+A.x+", y:"+A.y +" , Bx:"+B.x+", y:"+B.y);

        return annoys;
    }
    private static double find_annoy(Cor A, Cor B, boolean ball, int Player_id, Game game,int angle) {
        double annoys = 0;
        for (int i = 0; i < 5; i++) {
            if (i == Player_id) continue;
            double P_x = game.getMyTeam().getPlayer(i).getPosition().getX();
            double P_y = game.getMyTeam().getPlayer(i).getPosition().getY();
            if (ball) {
                if (dotline(P_x, P_y, A.x, A.y, B.x, B.y) < 0.75) {

                    if (Distance(A.x, A.y, B.x, B.y) <= Math.max(Distance(A.x, A.y, P_x, P_y), Distance(B.x, B.y, P_x, P_y))) {
                        annoys++;
                        
                    }
                }
            } else {
                if (dotline(P_x, P_y, A.x, A.y, B.x, B.y) < 1) {
                    if (Distance(A.x, A.y, B.x, B.y) <= Math.max(Distance(A.x, A.y, P_x, P_y), Distance(B.x, B.y, P_x, P_y))) {
                        annoys+=1.1-(Math.abs(0.5 -dotline(P_x, P_y, A.x, A.y, B.x, B.y)) *2);
                    }
                }
            }

        }
        for (int i = 0; i < 5; i++) {
            double P_x = game.getOppTeam().getPlayer(i).getPosition().getX();
            double P_y = game.getOppTeam().getPlayer(i).getPosition().getY();

            if (ball) {
                if (Math.abs(dotline(P_x, P_y, A.x, A.y, B.x, B.y)) < 0.75) {
                    if (Distance(A.x, A.y, B.x, B.y) <= Math.max(Distance(A.x, A.y, P_x, P_y), Distance(B.x, B.y, P_x, P_y))) {
                        annoys++;
                    }
                }
            } else {

                if (Math.abs(dotline(P_x, P_y, A.x, A.y, B.x, B.y)) < 1) {
                    if (Distance(A.x, A.y, B.x, B.y) <= Math.max(Distance(A.x, A.y, P_x, P_y), Distance(B.x, B.y, P_x, P_y))) {
                        annoys+=1.1-(Math.abs(0.5 -dotline(P_x, P_y, A.x, A.y, B.x, B.y)) *2);
                    }
                }
            }
        }
//        System.out.println("annoys:"+annoys+" , id:"+ Player_id +" , Ax:"+A.x+", y:"+A.y +" , Bx:"+B.x+", y:"+B.y);

        return annoys;
    }
    private static int annoy(Cor player_Cor, Cor Ball_cor, Cor Gate_Cor) {
        int annoy = 0;

        if (dotline(Ball_cor.x, Ball_cor.y, player_Cor.x, player_Cor.y, Gate_Cor.x, Gate_Cor.y) < 0.75)
            annoy++;
        return annoy;
    }

    private static int get_Score(Cor Player_Cor, Cor Ball_Cor, Cor Gate_Cor) {
        int score = 0;
        if (annoy(Player_Cor, Ball_Cor, Gate_Cor) == 1) {
            // own goal
            score = -99;
        }
        return score;
    }

    private static int get_Score(Cor Player_Cor, Cor Ball_Cor, Cor Gate_Cor, int angle, int i, int Player_id, Game game, Option option) {
        int score = 0;
        if (i == 0) {

            score -= find_annoy(Player_Cor, find_tok(Ball_Cor, Gate_Cor, true), false, Player_id, game) * 8;
            score -= find_annoy(Ball_Cor, Gate_Cor, true, Player_id, game) * 4;
            option.annoys = (int) (find_annoy(Player_Cor, find_tok(Ball_Cor, Gate_Cor, true), false, Player_id, game) +
                                find_annoy(Ball_Cor, Gate_Cor, true, Player_id, game,angle));
//            System.out.println("O_o: " + Gate_Cor.y);
            int tafazol_angle = Math.abs(find_angle( Player_Cor ,Ball_Cor) - find_angle(Ball_Cor, Gate_Cor));
            if (tafazol_angle > 180)
                tafazol_angle = 360 - tafazol_angle;
            score += 10 - (tafazol_angle / 9);
            double player_ball_distance = Distance(Player_Cor.x, Player_Cor.y, Ball_Cor.x, Ball_Cor.y);
            player_ball_distance += Distance(Ball_Cor.x, Ball_Cor.y, Gate_Cor.x, Gate_Cor.y);
            score -= (player_ball_distance/2); 
            option.distance = (int) Math.floor(player_ball_distance);
        } else if (i == 1) {
            // shoots himself to wall
            Cor player_shadow = find_Shadow_for_Player(Player_Cor, find_tok(Ball_Cor, Gate_Cor, true));
            if (player_shadow.x <= -7 || player_shadow.x >= 7) {
                score -= 9999;
            }
            score -= find_annoy(Player_Cor, player_shadow, false, Player_id, game) * 8;
            score -= find_annoy(player_shadow, Ball_Cor, false, Player_id, game) * 6;
            score -= find_annoy(Ball_Cor, Gate_Cor, true, Player_id, game) * 4;
            option.annoys = (int) (find_annoy(Player_Cor, player_shadow, false, Player_id, game) +
                                find_annoy(player_shadow, Ball_Cor, false, Player_id, game) +
                                find_annoy(Ball_Cor, Gate_Cor, true, Player_id, game));
            int tafazol_angle = Math.abs(find_angle(player_shadow, Ball_Cor) - find_angle(Ball_Cor, Gate_Cor));
            if (tafazol_angle > 180)
                tafazol_angle = 360 - tafazol_angle;
            score += 10 - (tafazol_angle / 9);
            double player_shadow_distance = Distance(Player_Cor.x, Player_Cor.y, player_shadow.x, player_shadow.y);
            // It should be separated to 2 parts:
            player_shadow_distance += Distance(player_shadow.x, player_shadow.y, Gate_Cor.x, Gate_Cor.y);
            score -= (player_shadow_distance/2);
            option.distance = (int) (player_shadow_distance);
        } else {
            Cor ball_shadow = find_Shadow_for_ball(Ball_Cor, Player_Cor, Gate_Cor);
            if (ball_shadow.x <= -7 || ball_shadow.x >= 7) {
                score -= 9999;
            }
            score -= find_annoy(Player_Cor, Ball_Cor, false, Player_id, game) * 8;
            score -= find_annoy(Ball_Cor, ball_shadow, true, Player_id, game) * 4;
            score -= find_annoy(ball_shadow, Gate_Cor, true, Player_id, game) * 4;
            option.annoys = (int) (find_annoy(Player_Cor, Ball_Cor, false, Player_id, game) +
                                find_annoy(Ball_Cor, ball_shadow, true, Player_id, game,angle) +
                                find_annoy(ball_shadow, Gate_Cor, true, Player_id, game,angle));
            int tafazol_angle = Math.abs(find_angle(Player_Cor, Ball_Cor)
                    - find_angle(Ball_Cor, ball_shadow));
            if (tafazol_angle > 180)
                tafazol_angle = 360 - tafazol_angle;
            score += 10 - (tafazol_angle / 9);
            double player_shadow_distance = Distance(Player_Cor.x, Player_Cor.y, ball_shadow.x, ball_shadow.y);
            // It should be separated to 2 parts:
            player_shadow_distance += Distance(ball_shadow.x, ball_shadow.y, Gate_Cor.x, Gate_Cor.y);
            score -= (player_shadow_distance/2);;
            option.distance = (int) Math.floor(player_shadow_distance);
        }
        if(score<4)
        score -= is_mohafez(Player_Cor , Ball_Cor, game);
        return score;
    }

    private static PriorityQueue<Option> Formula(Cor Player_Cor, Cor Ball_Cor, int player_id, Game game) {
        PriorityQueue<Option> priorityQueue = new PriorityQueue<>();
        for (int j = 0; j < 24; j++) {
            Cor Gate_Cor = new Cor(7, -1.15 + (j * 0.1));
            for (int i = 0; i < 3; i++) {

                if (i == 0) {
                    //direct shoot
                    int angle = find_angle(Player_Cor, find_tok(Ball_Cor, Gate_Cor, true));
                    Option option = new Option(angle, player_id);
                    option.score = get_Score(Player_Cor, Ball_Cor, Gate_Cor, angle, 0, player_id, game, option);
                    option.des = Gate_Cor;
                    priorityQueue.add(option);
                } else if (i == 1) {
                    // Player shoots himself to wall
                    Cor player_shadow = find_Shadow_for_Player(Player_Cor, find_tok(Ball_Cor, Gate_Cor, true));
                    int angle = find_angle(Player_Cor, player_shadow);
                    Option option = new Option(angle, player_id);
                    option.score = get_Score(Player_Cor, Ball_Cor, Gate_Cor, angle, 1, player_id, game, option);
                    option.des = player_shadow;
                    priorityQueue.add(option);

                } else {
                    // Player shoots ball to wall
                    Cor ball_shadow = find_Shadow_for_ball(Ball_Cor, Player_Cor, Gate_Cor);
                    int angle = find_angle(Player_Cor, find_tok(Ball_Cor, ball_shadow, true));
                    Option option = new Option(angle, player_id);
                    option.score = get_Score(Player_Cor, Ball_Cor, Gate_Cor, angle, 2, player_id, game, option);
                    option.des = ball_shadow;
                    priorityQueue.add(option);
                }
            }
        }
        return priorityQueue;
    }

    public static Triple do_turn(Game game) {
        int C = game.getCycle();
        int myturns=0;
        if(C%2==0)
            myturns=C/2;
        else
            myturns = (C+1)/2;
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
            priorityQueue.add(Formula(player_cor, ball_cor, i, game).poll());

        }
        
        for (Option option : priorityQueue) {
            System.out.println(option.player_id + ",score:" + option.score + ",angle:" + option.angle + ",distance:"
                    + option.distance + ",annoys:" + option.annoys+",des cor.x:"+option.des.x+",des.cor.y:"+option.des.y);
        }
       
        Option best_option = priorityQueue.poll();

        int My_Score = game.getMyTeam().getScore();
        int Opp_Score = game.getOppTeam().getScore();
        
        
        int Check_cycle = 3 + (rnd.nextInt() % 2);
        if (My_Score < Opp_Score && myturns % Check_cycle == 0 &&  priorityQueue.size()>=1 && C>20) {
            best_option = priorityQueue.poll();
            if(rnd.nextInt()%3==0 &&  priorityQueue.size()>=1)
                best_option = priorityQueue.poll();
        }
        else if(My_Score == Opp_Score && C>180 &&  priorityQueue.size()>=1)
        {
            best_option = priorityQueue.poll();
        }
        if (best_option == null) {
            // It should be completed
            PriorityQueue<Option> optionPriorityQueue = new PriorityQueue<>();
            Cor Ball_Cor = new Cor(game.getBall().getPosition().getX(), game.getBall().getPosition().getY());
            for (int oo = 0; oo < 5; oo++) {
                Cor Player_Cor = new Cor(game.getMyTeam().getPlayer(oo).getPosition().getX(), game.getMyTeam().getPlayer(oo).getPosition().getY());
                for (int j = 0; j < 24; j++) {
                    Cor Gate_Cor = new Cor(-7, -1.15 + (j * 0.1));
                    //direct shoot
                    int angle = find_angle(Player_Cor, Gate_Cor);
                    Option option = new Option(angle, oo);
                    option.score = get_Score(Player_Cor, Ball_Cor, Gate_Cor);
                    optionPriorityQueue.add(option);
                }
            }

            Option option = optionPriorityQueue.poll();
            if (option.score == -99) {
                if (option.angle != 120) {
                    act.setAngle(120);
                } else {
                    act.setAngle(220);
                }
            } else {
                act.setAngle(option.angle);
            }
            if (game.getMyTeam().getPlayer(option.player_id).getPosition().getX() > 2) {
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

            System.out.println("lala,"+best_option.player_id +",score:" + best_option.score + ",angle:" + best_option.angle + ",distance:"
                    + best_option.distance + ",annoys:" + best_option.annoys+",des cor.x:"+best_option.des.x+",des.cor.y:"+best_option.des.y);
            act.setAngle(best_option.angle);
            act.setPower(100);
            act.setPlayerID(best_option.player_id);
            System.out.println("Cycle finished\n");
            return act;
        }
    }

}

/**

    private static int get_Score(Cor Player_Cor, Cor Ball_Cor, Cor Gate_Cor, int angle, int i, int Player_id) {
        int score = 0;
        boolean b = false;
        if (Player_id == 0 || Player_id == 4) {
            b = true;
        }
        if (is_mohafez(Player_Cor)) {
            if (b) {
                System.out.println("score of " + Player_id + ", is reducing!GATE::" + score);
            }
            score -= 3;
        }
        if (i == 0) {
            score -= find_annoy(Player_Cor, find_tok(Ball_Cor, Gate_Cor, true), false, Player_id) * 4;
            if (b) {
                System.out.println("score of " + Player_id + ", is reducing!annoy_player_ball::" + score);
            }
            score -= find_annoy(Ball_Cor, Gate_Cor, true, Player_id) * 3;
            if (b) {
                System.out.println("score of " + Player_id + ", is reducing!:ball_gate:" + score);
            }
            score += 9 - (Math.abs(angle - find_angle(Ball_Cor, Gate_Cor)) / 10);
            if (b) {
                System.out.println("score of " + Player_id + ", is reducing!:angle:" + score + ",it's angle:" + angle);
            }
            double player_ball_distance = Distance(Player_Cor.x, Player_Cor.y, Ball_Cor.x, Ball_Cor.y);
            player_ball_distance += Distance(Ball_Cor.x, Ball_Cor.y, Gate_Cor.x, Gate_Cor.y);
            score += (int) Math.floor(player_ball_distance * (-1));
            if (b) {
                System.out.println("score of " + Player_id + ", is reducing!:distance:" + score);
            }

        } else if (i == 1) {
            double x, y;
            // shoots himself to wall
            Cor player_shadow = find_Shadow_for_Player(Player_Cor, Ball_Cor);
            if (player_shadow.x <= -7 || player_shadow.x >= 7) {
                score -= 9999;
            }
            score -= find_annoy(Player_Cor, player_shadow, false, Player_id) * 4;
            score -= find_annoy(player_shadow, Ball_Cor, false, Player_id) * 3;
            score -= find_annoy(Ball_Cor, Gate_Cor, true, Player_id) * 3;
            score += 9 - (Math.abs(find_angle(player_shadow, Ball_Cor) - find_angle(Ball_Cor, Gate_Cor)) / 10);
            double player_shadow_distance = Distance(Player_Cor.x, Player_Cor.y, player_shadow.x, player_shadow.y);
            // It should be separated to 2 parts:
            player_shadow_distance += Distance(player_shadow.x, player_shadow.y, Gate_Cor.x, Gate_Cor.y);
            score += (int) Math.floor(player_shadow_distance * (-1));

        } else {
            Cor ball_shadow = find_Shadow_for_ball(Player_Cor, Ball_Cor);
            if (ball_shadow.x <= -7 || ball_shadow.x >= 7) {
                score -= 9999;
            }
            score -= find_annoy(Player_Cor, Ball_Cor, false, Player_id) * 4;
            score -= find_annoy(Ball_Cor, ball_shadow, true, Player_id) * 3;
            score -= find_annoy(ball_shadow, Gate_Cor, true, Player_id) * 3;
            score += 9 - (Math.abs(find_angle(Player_Cor, Ball_Cor)
                    - find_angle(Ball_Cor, find_Shadow_for_ball(Player_Cor, Ball_Cor))) / 10);
            double player_shadow_distance = Distance(Player_Cor.x, Player_Cor.y, ball_shadow.x, ball_shadow.y);
            // It should be separated to 2 parts:
            player_shadow_distance += Distance(ball_shadow.x, ball_shadow.y, Gate_Cor.x, Gate_Cor.y);
            score += (int) Math.floor(player_shadow_distance * (-1));

        }
        return score;
    }

*     private static PriorityQueue<Option> Formula(Cor Player_Cor, Cor Ball_Cor, int player_id) {
        PriorityQueue<Option> priorityQueue = new PriorityQueue<>();
        for (int j = 0; j < 24; j++) {
            Cor Gate_Cor = new Cor(7, -1.15 + (j * 0.1));
            for (int i = 0; i < 3; i++) {
                if (i == 0) {
                    //direct shoot
                    int angle = find_angle(Player_Cor, find_tok(Ball_Cor, Gate_Cor, true));
                    Option option = new Option(angle, player_id);
                    option.score = get_Score(Player_Cor, Ball_Cor, Gate_Cor, angle, i, player_id);
                    priorityQueue.add(option);
                } else if (i == 1) {
                    // Player shoots himself to wall
                    Cor player_shadow = find_Shadow_for_Player(Player_Cor, find_tok(Ball_Cor, Gate_Cor, true));
                    int angle = find_angle(Player_Cor, player_shadow);
                    Option option = new Option(angle, player_id);
                    option.score = get_Score(Player_Cor, Ball_Cor, Gate_Cor, angle, i, player_id);
                    priorityQueue.add(option);

                } else {
                    // Player shoots ball to wall
                    if (Player_Cor.y < Ball_Cor.y) {
                        Cor mirror_gate = new Cor(Gate_Cor.x, 7.5 - Gate_Cor.y);
                        Cor ball_shadow = find_Shadow_for_ball(Player_Cor, find_tok(Ball_Cor, mirror_gate, true));
                        int angle = find_angle(Player_Cor, ball_shadow);
                        Option option = new Option(angle, player_id);
                        option.score = get_Score(Player_Cor, Ball_Cor, Gate_Cor, angle, i, player_id);
                        priorityQueue.add(option);
                    } else {
                        Cor mirror_gate = new Cor(Gate_Cor.x, -7.5 + Gate_Cor.y);
                        Cor B = find_Shadow_for_ball(Player_Cor, find_tok(Ball_Cor, mirror_gate, true));
                        int angle = find_angle(Player_Cor, B);
                        Option option = new Option(angle, player_id);
                        option.score = get_Score(Player_Cor, Ball_Cor, Gate_Cor, angle, i, player_id);
                        priorityQueue.add(option);
                    }
                }
            }
        }
        return priorityQueue;
    }

    static Triple do_turn(Game game) {
        Triple act = new Triple();
        Team team = game.getMyTeam();
        for (int i = 0; i < 5; i++) {
            players[i] = team.getPlayer(i);
        }
        Team enemy_team = game.getOppTeam();
        for (int i = 0; i < 5; i++) {
            enemies[i] = enemy_team.getPlayer(i);
        }
        double x2, y2;
        x2 = game.getBall().getPosition().getX();
        y2 = game.getBall().getPosition().getY();
        Cor ball_cor = new Cor(x2, y2);

        PriorityQueue<Option> priorityQueue = new PriorityQueue<>();
        for (int i = 0; i < 5; i++) {
            Cor player_cor = new Cor(players[i].getPosition().getX(), players[i].getPosition().getY());
            if (player_cor.x > ball_cor.x) {
                continue;
            }
            priorityQueue.add(Formula(player_cor, ball_cor, i).poll());

        }
        System.out.println("size1:" + priorityQueue.size());
        for (Option option : priorityQueue) {
            System.out.println(option.player_id + ",score:" + option.score + ",angle:" + option.angle);
        }
        System.out.println("size:" + priorityQueue.size());
        Option best_option = priorityQueue.poll();

        if (best_option == null) {
            // It should be completed
            PriorityQueue<Option> optionPriorityQueue = new PriorityQueue<>();
            Cor Ball_Cor = new Cor(game.getBall().getPosition().getX(), game.getBall().getPosition().getY());
            for (int oo = 0; oo < 5; oo++) {
                Cor Player_Cor = new Cor(players[oo].getPosition().getX(), players[oo].getPosition().getY());
                for (int j = 0; j < 24; j++) {
                    Cor Gate_Cor = new Cor(-7, -1.15 + (j * 0.1));
                    //direct shoot
                    int angle = find_angle(Player_Cor, Gate_Cor);
                    Option option = new Option(angle, oo);
                    option.score = get_Score(Player_Cor, Ball_Cor, Gate_Cor);
                    optionPriorityQueue.add(option);
                }
            }

            Option option = optionPriorityQueue.poll();
            if (option.score == -99) {
                if (option.angle != 120) {
                    act.setAngle(120);
                } else {
                    act.setAngle(220);
                }
            } else {
                act.setAngle(option.angle);
            }
            if (players[option.player_id].getPosition().getX() > 2) {
                act.setPower(100);
            } else {
                act.setPower((int) ((11) * Distance(players[option.player_id].getPosition().getX(), players[option.player_id].getPosition().getY(), -7, 0)));
            }
            act.setPlayerID(option.player_id);
            return act;
        } else {
            act.setAngle(best_option.angle);
            act.setPower(100);
            act.setPlayerID(best_option.player_id);
            return act;
        }

    }
* */
// -=-=-=-=-=-=-=-=-=-=-=-=The end

/**
* getScore Function:
*
*
        if (i == 0){
            // shoot directly
            // process: 1.find annoys , 2.calculate the difference between player_ball angle and ball_gate angle
            // process: 3.calculate distance from player to ball and ball to gate
            int annoys = find_annoy(Player_Cor,Ball_Core,true);
            annoys += find_annoy(Ball_Core,Gate_Core,true);
            Cor Ball_tok = find_tok(Ball_Core,Gate_Core,true);
            int player_ball_angle = find_angle(Player_Cor, Ball_tok);
            int ball_gate_angle = find_angle(Ball_tok, Gate_Core);
            int difference_angle = Math.abs(player_ball_angle - ball_gate_angle);
            double player_ball_distance = Distance(Player_Cor.x,Player_Cor.y, Ball_Core.x, Ball_Core.y);
            score = (int) Math.floor(player_ball_distance*(-0.3) + annoys*(-3) + difference_angle*(-0.1));

        }else if (i == 1){
            // player shoots himself to wall
            // process: 1.find annoys , 2.calculate the difference between player_ball angle and ball_gate angle
            // process: 3.calculate distance from player to ball and ball to gate

            Cor player_shadow = find_Shadow_for_Player(Player_Cor,find_tok(Ball_Core,Gate_Core,true));
            int player_shadow_angle = find_angle(Player_Cor,player_shadow);
            int annoys = find_annoy(Player_Cor,player_shadow,true);
            annoys += find_annoy(player_shadow,,true);
            int player_ball_angle = find_angle(Player_Cor, Ball_Core);

            double player_ball_distance = Distance(Player_Cor.x,Player_Cor.y, player_shadow.x, player_shadow.y);
            player_ball_distance += Distance(player_shadow.x, player_shadow.y, Gate_Core.x, Gate_Core.y);
            score = (int) Math.floor(player_ball_distance*(-0.3) + annoys*(-3) + difference_angle*(-0.1));

        }else{

        }

**/