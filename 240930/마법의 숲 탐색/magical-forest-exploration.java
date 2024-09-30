/*
1.골렘 이동!!
1.1. 골렘 남쪽으로 이동
1.2. 1안되면 왼쪽으로 이동하고 내려감(출구바뀜)
1.3. 2도 안되면 오른쪽으로 이동하고 내려감(출구바뀜)

2.
 */
import java.util.*;
import java.io.*;
class Golram{
    int x;
    int y;
    int exit;
    public Golram(int x,int y, int exit){
        this.x = x;
        this.y = y;
        this.exit = exit;
    }
}
public class Main {
    static int R, C, K,rMax;
    static int[][] map;
    static boolean[] visited; //moveGolam할 때, 이미 거쳐간 골렘은 안가게 하기 위해
    static Golram[] golrams;
    static int[] dx = {0,1,0,-1};
    static int[] dy = {-1,0,1,0};
    static int result=0;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        initMap(R, C);
        golrams = new Golram[K];
        for (int i = 0; i < K; i++) {
            rMax = 0;
            visited = new boolean[K];
            st = new StringTokenizer(br.readLine());
            int c = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken());
            simulation(i, -1, c, d);

        }
        System.out.println(result);
    }
    public static void simulation(int idx,int y,int x, int d){
        while(true){
            if(y== R-1){ //바닥에 도착
                break;
            }
            //1.남쪽으로 이동
            //1.1 양옆 가운데 아래 비었는지 확인
            if((y==-1&& map[y+2][x]==-1) || map[y+1][x-1] ==-1 && map[y+2][x] ==-1 && map[y+1][x+1]==-1){
                y+=1;
                continue;
            }
            //2.서쪽이동후 남쪽으로이동 (출구 반시계방향으로 바뀜)
            if(x>=3){
                if((y==-1 && map[y+2][x-1]==-1)|| (y==0 && map[y+2][x-1]==-1 &&map[y+1][x-1]==-1 && map[y+1][x-2]==-1) || (map[y + 2][x - 1] == -1 && map[y + 1][x - 1] == -1 && map[y + 1][x - 2] == -1 && map[y][x - 2] == -1)) {
                    x -=1;
                    y +=1;
                    d = (d +3)%4;
                    continue;
                }
            }
            //3.동쪽이동후 남쪽으로 이동(출구 시계방향으로 바뀜)
            if(x<=C-2){
                if((y==-1 && map[y+2][x+1]==-1)||(y==0 && map[y+1][x+2]==-1 &&map[y+1][x+1]==-1 && map[y+2][x+1]==-1) ||(map[y + 2][x + 1] == -1 && map[y + 1][x + 1] == -1 && map[y + 1][x + 2] == -1 && map[y][x + 2] == -1)) {
                    x+=1;
                    y+=1;
                    d = (d + 1)%4;
                    continue;
                }
            }
            break;
        }
        //y가 2보다 작으면 밖으로 나와있는거라 map 리셋
        if(y <=1){
            initMap(R,C);
            return;
        }
        //골렘 저장 map이랑 Golram에
        golrams[idx] = new Golram(x,y,d);
        for(int i = 0; i<4; i++){
            int ny = y +dy[i];
            int nx = x +dx[i];
            if(isRange(nx,ny)){
                map[ny][nx] =idx;
            }

        }
        moveGolam(idx);
        result += rMax;
    }
    public static void moveGolam(int index){
        visited[index] = true;
        int exit = golrams[index].exit;
        int floor = golrams[index].y +1;
        rMax = Math.max(rMax,floor);

        //출구 위치로 이동
        int ry = golrams[index].y + dy[exit];
        int rx = golrams[index].x + dx[exit];

        //출구 근처 다른 골렘 찾기
        for(int i =0; i<4; i++){
            int ny = ry +dy[i];
            int nx = rx +dx[i];

            //범위 밖이면 continue
            if(ny<1 ||ny>R || nx <1 || nx >C) continue;
            //이미 방문 골렘이거나 골렘이 없으면 continue
            if(map[ny][nx] ==-1 || visited[map[ny][nx]]) continue;

            moveGolam(map[ny][nx]);
        }
    }
    public static void initMap(int r,int c){
        map = new int[r+1][];
        for(int i =0; i<r+1; i++){
            map[i] = new int[c+1];
            Arrays.fill(map[i],-1);
        }

    }
    public static boolean isRange(int x,int y){
        return (x>0 &&x<=C &&y>0 && y<=R);
    }
}