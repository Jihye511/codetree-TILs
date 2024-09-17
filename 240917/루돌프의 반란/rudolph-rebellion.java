import java.io.*;
import java.util.*;

class Santa{
    int x;
    int y;
    public Santa(int x, int y){
        this.x = x;
        this.y = y;
    }
}
public class Main{
    // 상우하좌
    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, 1, 0, -1};
    static int N,M,P,C,D;
    static int[][] map; // 산타 루돌프 위치 저장 (산타 번호, 루돌프 -1)
    static Santa[] santa;
    static int[] stun; //산타 기절 확
    static boolean[] dead; //산타 생사 확인
    static int Rr,Rc;
    static int[] score;
    public static void main(String[] args) throws IOException{
        BufferedReader br =new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        P = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        D = Integer.parseInt(st.nextToken());

        score = new int[P+1];
        map = new int[N][N];
        santa = new Santa[P+1];
        stun = new int[P+1];
        dead = new boolean[P+1];

        st = new StringTokenizer(br.readLine());
        //루돌프 위치
        Rr = Integer.parseInt(st.nextToken())-1;
        Rc = Integer.parseInt(st.nextToken())-1;
        map[Rr][Rc] = -1;
        //산타 위치
        for(int i =0; i<P; i++){
            st = new StringTokenizer(br.readLine());
            int num = Integer.parseInt(st.nextToken());
            int x=Integer.parseInt(st.nextToken())-1;
            int y=Integer.parseInt(st.nextToken())-1;
            santa[num] = new Santa(x,y);
            map[x][y] = num;
        }
        while(M -->0){
            int minDist = Integer.MAX_VALUE; //가장 가까운 산타와의 거리
            int minX = 10000; // 최소 거리의 산타의 x 좌표
            int minY = 10000; // 최소 거리의 산타의 y 좌표
            int minId = 0; // 최소 거리 산타의 번호

            //1. 가장 가까운 산타 찾기
            for(int i =1; i<=P; i++) {
                //죽은 건 제외
                if (dead[i]) continue;

                //루돌프와 산타 거리 계산
                int dist = (int) (Math.pow(santa[i].x - Rr, 2) + Math.pow(santa[i].y - Rc, 2));

                //거리비교
                if (dist < minDist) {
                    minDist = dist;
                    minX = santa[i].x;
                    minY = santa[i].y;
                    minId = i;
                } else if (dist == minDist) {
                    //같다면 x가 큰 순, y가 큰 순으로 정렬
                    if (santa[i].x > minX || (santa[i].x == minX && santa[i].y > minY)) {
                        minX = santa[i].x;
                        minY = santa[i].y;
                        minId = i;
                    }
                }
            }

            //2.루돌프가 가장 가까운 산타에게 돌진
            if(minId != 0){
                moveRudolph(minX,minY,minId);
            }

            //3. 산타가 루돌프와 가장 가까운 방향으로움직임
            moveSanta();
            //4. 죽지 않은 산타의 점수 증가
            getScore();
            //5. 기절한 산타으 ㅣ턴 수 감소
            decreaseTurn();
        }
        // 산타가 얻은 최종점수 출력
        for (int i = 1; i <= P; i++) {
            System.out.print(score[i] + " ");
        }
    }
    //루돌프 이동
    public static void moveRudolph(int x, int y, int id){
        int moveX = 0;
        if(x >Rr){
            moveX = 1;
        }else if(x<Rr){
            moveX =-1;
        }
        int moveY = 0;
        if(y>Rc){
            moveY =1;
        }else if(y <Rc){
            moveY = -1;
        }

        map[Rr][Rc] = 0;

        //가장 가까운 산타로 이동
        Rr += moveX;
        Rc += moveY;

        //루돌프가 움직여서 산타와 충돌한 경우
        if(Rr ==x && Rc ==y){
            //루돌프가 이동해온 방향으로 c칸 움직임
            int firstX = x +moveX*C;
            int firstY = y +moveY*C;

            //충돌이 일어나서 상호작용을 위해 저장
            int lastX = firstX;
            int lastY = firstY;

            stun[id] = 2; //기절시킴
            //격자 범우 ㅣ안이고 다른 산타가 있는 경우 연쇄적 충돌
            while (isRange(lastX,lastY) && map[lastX][lastY]>0){
                lastX +=moveX;
                lastY +=moveY;
            }

            //연쇄적 충돌후 위치로 산타 이동시키기
            while(!(lastX == firstX && lastY == firstY)){
                int prevX = lastX - moveX;
                int prevY = lastY - moveY;

                // 범위를 벗어나는 경우 종료
                if (!isRange(prevX, prevY)) {
                    break;
                }
                int idx = map[prevX][prevY];
                if (isRange(lastX, lastY)) {
                    // 격자 안인 경우 이전 칸에 있던 산타를 현재 칸으로 옮겨줌
                    map[lastX][lastY] = idx;
                    santa[idx] = new Santa(lastX, lastY);
                } else {
                    dead[idx] = true; // 격자 밖을 벗어나는 경우 죽은 표시 해줌
                }
                lastX = prevX;
                lastY = prevY;
            }
            //해당 산타 점수 증가시키고 위치 옮김
            score[id] +=C;
            if(isRange(firstX,firstY)){
                map[firstX][firstY] = id;
                santa[id] =new Santa(firstX,firstY);
            }else {
                dead[id] =true;
            }
        }
        map[Rr][Rc] = -1;
    }
    //산타 이동
    public static void moveSanta(){
        for(int i =1; i<=P; i++) {
            if (dead[i] || stun[i] > 0) continue;

            int minDist = (int) (Math.pow(santa[i].x - Rr, 2) + Math.pow(santa[i].y - Rc, 2));
            int moveDir = -1;
            // 방향 우선순위에 따라 상우하좌 4방향 이동해보면서 루돌프와 가장 가까운 방향 찾음
            for (int d = 0; d < 4; d++) {
                int nx = santa[i].x + dx[d];
                int ny = santa[i].y + dy[d];

                if (!isRange(nx, ny) || map[nx][ny] > 0) continue;

                int dist = (int) (Math.pow(nx - Rr, 2) + Math.pow(ny - Rc, 2));

                if (dist < minDist) {
                    minDist = dist;
                    moveDir = d;
                }
            }
            if (moveDir != -1) {
                //다음이동 위치
                int nx = santa[i].x + dx[moveDir];
                int ny = santa[i].y + dy[moveDir];

                //충돌시 기절
                if (nx == Rr && ny == Rc) {
                    stun[i] = 2;

                    int moveX = -dx[moveDir];
                    int moveY = -dy[moveDir];

                    int firstX = nx + moveX * D;
                    int firstY = ny + moveY * D;

                    int lastX = firstX;
                    int lastY = firstY;

                    if (D == 1) {
                        score[i] += D;
                    } else {
                        //격자 안이고 다른 산타있는 경우
                        while (isRange(lastX, lastY) && map[lastX][lastY] > 0) {
                            lastX +=moveX;
                            lastY +=moveY;
                        }
                        while(!(lastX == firstX && lastY == firstY)) {
                            int preX = lastX - moveX;
                            int preY = lastY - moveY;

                            if (!isRange(preX, preY)) break;

                            int idx = map[preX][preY];
                            if(isRange(lastX,lastY)){
                                map[lastX][lastY] = idx;
                                santa[idx] = new Santa(lastX,lastY);
                            }else{
                                dead[idx] =true;
                            }

                            lastX = preX;
                            lastY = preY;
                        }
                        score[i] +=D;

                        map[santa[i].x][santa[i].y] = 0;
                        if (isRange(firstX, firstY)) {
                            map[firstX][firstY] = i;
                            santa[i] = new Santa(firstX, firstY);
                        } else {
                            dead[i] = true;
                        }
                    }
                }else { //충ㄷ돌하지 않은 경우
                    map[santa[i].x][santa[i].y] = 0; // 원래 산타가 있던 위치는 빈칸으로 만듦

                    // 해당 산타 위치 갱신
                    santa[i] = new Santa(nx, ny);
                    map[nx][ny] = i;
                }
            }
        }

    }
    //산타 점수 증가
    public static void getScore(){
        for(int i=1; i<=P; i++){
            if(!dead[i]){
                score[i]++;
            }
        }
    }

    //기절한 산타 턴스 감소
    public static void decreaseTurn(){
        for(int i =1; i<=P;i++){
            if(stun[i]>0){
                stun[i]--;
            }
        }
    }

    //범위안?
    public static boolean isRange(int x, int y){
        return x>=0 &&x<N && y>=0 && y<N;
    }
}