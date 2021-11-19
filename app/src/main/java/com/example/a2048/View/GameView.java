package com.example.a2048.View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.*;
import android.widget.GridLayout;
import android.widget.Toast;
import com.example.a2048.PlayActivity;
import com.example.a2048.R;

import java.util.Random;

/**
 * @author lmw
 */
public class GameView extends GridLayout {
    public static GameView gameView;

    private com.example.a2048.View.Card[][] cards = new com.example.a2048.View.Card[4][4];

    /**
     * 重载初始化函数
     *
     * @param context
     */
    public GameView(Context context) {
        super(context);
        gameView = this;
        initGame();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gameView = this;
        initGame();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gameView = this;
        initGame();
    }

    /**
     * 游戏初始化
     */
    public void initGame() {
        this.setBackgroundColor(getResources().getColor(R.color.gameViewBackgroundColor, null));
        setColumnCount(4);

        int cardWidth = GetCardWidth();
        addCards(cardWidth, cardWidth);

        randomCreateCard(2);

        setListener();
    }

    /**
     * 重新开始游戏
     */
    public void replayGame() {
        PlayActivity.playActivity.clearScore();
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                cards[i][j].setNum(0);
            }
        }
        randomCreateCard(2);
    }

    /**
     * 监听Touch事件，主要监听页面滑动，通过将结束的x，y和刚开始时的x，y值比较，记录移动的参数，来判断是水平还是垂直移动
     * 10作为判断是否是有效移动的标准
     */
    private void setListener() {
        setOnTouchListener(new OnTouchListener() {
            private float staX, staY, endX, endY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        staX = event.getX();
                        staY = event.getY();
                        break;

                    case MotionEvent.ACTION_UP:
                        endX = event.getX();
                        endY = event.getY();

                        boolean swiped = false;//记录是否有效滑动了

                        //水平移动更多
                        if (Math.abs(endX - staX) > Math.abs(endY - staY)) {
                            if (endX - staX > 10) {
                                if (swipeRight()) {
                                    swiped = true;
                                }
                            } else if (endX - staX < -10) {
                                if (swipeLeft()) {
                                    swiped = true;
                                }
                            }
                        } else {
                            if (endY - staY < -10) {
                                if (swipeUp()) {
                                    swiped = true;
                                }
                            } else if (endY - staY > 10) {
                                if (swipeDown()) {
                                    swiped = true;
                                }
                            }
                        }
                        //滑动后创建新块，并检查当前状态是否能滑动
                        if (swiped) {
                            randomCreateCard(1);
                            if (!canSwipe()) {
                                gameOver();
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 返回该次滑动是否有效（有卡片移动或合并），上移
     */
    private boolean swipeUp() {
        boolean flag = false;
        for (int j = 0; j < 4; ++j) {
            int ind = 0;
            //从上往下依次处理
            for (int i = 1; i < 4; ++i) {
                //如果是存在数字的，往上遍历
                if (cards[i][j].getNum() != 0) {
                    for (int ii = i - 1; ii >= ind; --ii) {
                        //如果这块是空的，将数字上移
                        if (cards[ii][j].getNum() == 0) {
                            cards[ii][j].setNum(cards[i][j].getNum());
                            cards[i][j].setNum(0);
                            i--;//上移
                            flag = true;
                        }
                        //如果这块是相同的数，合并，合并的块不能一下合并两次，更新ind，不再遍历合并的块
                        else if (cards[ii][j].getNum() == cards[i][j].getNum()) {
                            cards[ii][j].setNum((cards[i][j].getNum() * 2));
                            cards[i][j].setNum(0);
                            flag = true;
                            ind = ii + 1;//已经合过，该点不再合成
                            PlayActivity.playActivity.addScore(cards[ii][j].getNum() / 2);
                            //播放合并动画
                            playMergeAnimation(ii, j);
                            break;
                        }
                        //上面的块数字不同，退出循环
                        else {
                            break;
                        }
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 下移
     *
     * @return
     */
    private boolean swipeDown() {
        boolean flag = false;
        for (int j = 0; j < 4; ++j) {
            int ind = 4;
            for (int i = 2; i >= 0; --i) {
                if (cards[i][j].getNum() != 0) {
                    for (int ii = i + 1; ii < ind; ++ii) {
                        if (cards[ii][j].getNum() == 0) {
                            cards[ii][j].setNum(cards[i][j].getNum());
                            cards[i][j].setNum(0);
                            flag = true;
                            i++;
                        } else if (cards[ii][j].getNum() == cards[i][j].getNum()) {
                            cards[ii][j].setNum((cards[i][j].getNum() * 2));
                            cards[i][j].setNum(0);
                            flag = true;
                            ind = ii;
                            PlayActivity.playActivity.addScore(cards[ii][j].getNum() / 2);
                            playMergeAnimation(ii, j);
                            break;
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 左移
     *
     * @return
     */
    private boolean swipeLeft() {
        boolean flag = false;
        for (int i = 0; i < 4; ++i) {
            int ind = 0;
            for (int j = 1; j < 4; ++j) {
                if (cards[i][j].getNum() != 0) {
                    for (int jj = j - 1; jj >= ind; --jj) {
                        if (cards[i][jj].getNum() == 0) {
                            cards[i][jj].setNum(cards[i][j].getNum());
                            cards[i][j].setNum(0);
                            flag = true;
                            j--;
                        } else if (cards[i][jj].getNum() == cards[i][j].getNum()) {
                            cards[i][jj].setNum((cards[i][j].getNum() * 2));
                            cards[i][j].setNum(0);
                            flag = true;
                            ind = jj + 1;
                            PlayActivity.playActivity.addScore(cards[i][jj].getNum() / 2);
                            playMergeAnimation(i, jj);
                            break;
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 右移
     *
     * @return
     */
    private boolean swipeRight() {
        boolean flag = false;
        for (int i = 0; i < 4; ++i) {
            int ind = 4;
            for (int j = 2; j >= 0; --j) {
                if (cards[i][j].getNum() != 0) {
                    for (int jj = j + 1; jj < ind; ++jj) {
                        if (cards[i][jj].getNum() == 0) {
                            cards[i][jj].setNum(cards[i][j].getNum());
                            cards[i][j].setNum(0);
                            flag = true;
                            j++;
                        } else if (cards[i][jj].getNum() == cards[i][j].getNum()) {
                            cards[i][jj].setNum((cards[i][j].getNum() * 2));
                            cards[i][j].setNum(0);
                            flag = true;
                            ind = jj;
                            PlayActivity.playActivity.addScore(cards[i][jj].getNum() / 2);
                            playMergeAnimation(i, jj);
                            break;
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 如果存在空白块，或者相邻的数字相同的块，则可以继续滑动
     */
    private boolean canSwipe() {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                if (cards[i][j].getNum() == 0) {
                    return true;
                } else if (i != 3 && cards[i][j].getNum() == cards[i + 1][j].getNum()) {
                    return true;
                } else if (j != 3 && cards[i][j].getNum() == cards[i][j + 1].getNum()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 添加卡片
     *
     * @param width
     * @param height
     */
    private void addCards(int width, int height) {
        com.example.a2048.View.Card c;
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                c = new com.example.a2048.View.Card(getContext());
                addView(c, width, height);
                cards[i][j] = c;
            }
        }
    }

    private void gameOver() {
        Toast.makeText(getContext(), "游戏结束", Toast.LENGTH_SHORT).show();
        PlayActivity.playActivity.over();
    }

    /**
     * 获取屏幕信息，来计算卡片数宽度
     *
     * @return
     */
    private int GetCardWidth() {
        //获取屏幕信息
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        //根据布局，GameView是占屏幕宽度的90%，除以4就是卡片边长
        return (int) ((displayMetrics.widthPixels * 0.9f) / 4);
    }

    /**
     * 递归随机，
     * 最好是把可用方块加入到一个列表中，然后在列表中随机
     */
    private void randomCreateCard(int cnt) {
        Random random = new Random();
        int r = random.nextInt(4);
        int c = random.nextInt(4);

        //该处已经存在数字，重新随机r, c
        if (cards[r][c].getNum() != 0) {
            randomCreateCard(cnt);
            return;
        }

        int rand = random.nextInt(10);

        if (rand >= 2) {
            rand = 2;
        } else {
            rand = 4;
        }

        cards[r][c].setNum(rand);

        //播放创建动画
        playCreateAnimation(r, c);

        if (cnt >= 2) {
            randomCreateCard(cnt - 1);
        }
    }

    /**
     * 播放创建新块动画，
     * 360°旋转，0.5f围绕中心点旋转
     * 通过设置开始和结束的缩放因子来实现缩放，0.5f中心点开始缩放
     */
    private void playCreateAnimation(int r, int c) {
        AnimationSet animationSet = new AnimationSet(true);

        //旋转
        RotateAnimation anim = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(250);
        anim.setRepeatCount(0);
        anim.setInterpolator(new LinearInterpolator());

        //缩放
        ScaleAnimation anim2 = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        anim2.setDuration(250);
        anim2.setRepeatCount(0);

        animationSet.addAnimation(anim);
        animationSet.addAnimation(anim2);

        cards[r][c].startAnimation(animationSet);
    }

    /**
     * 播放合并动画
     */
    private void playMergeAnimation(int r, int c) {
        ScaleAnimation anim = new ScaleAnimation(1, 1.2f, 1, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        anim.setDuration(150);
        anim.setRepeatCount(0);

        anim.setRepeatMode(Animation.REVERSE);

        cards[r][c].startAnimation(anim);
    }
}

