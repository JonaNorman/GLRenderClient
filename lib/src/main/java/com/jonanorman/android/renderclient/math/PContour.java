package com.jonanorman.android.renderclient.math;

import java.util.ArrayList;

public class PContour {

    static final int N_PIXEL_NEIGHBOR = 8;


    /**
     * 找到i,j点的第i邻域坐标
     *
     * @param i  坐标
     * @param j  坐标
     * @param id 第i邻域 以中下的点为第一个点，逆时针旋转
     * @return 领域坐标int[2]
     */
    static int[] neighborIDToIndex(int i, int j, int id) {
        if (id == 0) {
            return new int[]{i, j + 1};
        }
        if (id == 1) {
            return new int[]{i - 1, j + 1};
        }
        if (id == 2) {
            return new int[]{i - 1, j};
        }
        if (id == 3) {
            return new int[]{i - 1, j - 1};
        }
        if (id == 4) {
            return new int[]{i, j - 1};
        }
        if (id == 5) {
            return new int[]{i + 1, j - 1};
        }
        if (id == 6) {
            return new int[]{i + 1, j};
        }
        if (id == 7) {
            return new int[]{i + 1, j + 1};
        }
        return null;
    }

    /**
     * 找到i,j点相对于i0,j0是第几领域
     *
     * @param i0 中心坐标(i0,j0)的坐标x
     * @param j0 中心坐标(i0,j0)的坐标y
     * @param i  领域坐标(i,j)的坐标y
     * @param j  领域坐标(i,j)的坐标y
     * @return 第几领域
     */
    static int neighborIndexToID(int i0, int j0, int i, int j) {
        int di = i - i0;
        int dj = j - j0;
        if (di == 0 && dj == 1) {
            return 0;
        }
        if (di == -1 && dj == 1) {
            return 1;
        }
        if (di == -1 && dj == 0) {
            return 2;
        }
        if (di == -1 && dj == -1) {
            return 3;
        }
        if (di == 0 && dj == -1) {
            return 4;
        }
        if (di == 1 && dj == -1) {
            return 5;
        }
        if (di == 1 && dj == 0) {
            return 6;
        }
        if (di == 1 && dj == 1) {
            return 7;
        }
        return -1;
    }

    /**
     * 找到从相对于(i0,j0)中心坐标的领域坐标(i,j)逆时针找F[]数据里面第一个非0的坐标
     *
     * @param F      每个点的数据
     * @param w      宽
     * @param h      高
     * @param i0     (i0,j0)中心坐标
     * @param j0     (i0,j0)中心坐标
     * @param i      领域坐标(i,j)
     * @param j      领域坐标(i,j)
     * @param offset 偏移量
     * @return 第一个非0领域坐标int[2]
     */
    static int[] ccwNon0(int[] F, int w, int h, int i0, int j0, int i, int j, int offset) {
        int id = neighborIndexToID(i0, j0, i, j);
        for (int k = 0; k < N_PIXEL_NEIGHBOR; k++) {
            int kk = (k + id + offset + N_PIXEL_NEIGHBOR * 2) % N_PIXEL_NEIGHBOR;//逆时针，为什么要加2*N_PIXEL_NEIGHBOR，
            // 大概率是为了保证输出是kk是正数，但是不知道为什么要*2而不是*其他
            int[] ij = neighborIDToIndex(i0, j0, kk);
            if (F[ij[0] * w + ij[1]] != 0) {//非0
                return ij;
            }
        }
        return null;
    }

    /**
     * 找到从相对于(i0,j0)中心坐标的领域坐标(i,j)顺时针找F[]数据里面第一个非0的坐标
     *
     * @param F
     * @param w
     * @param h
     * @param i0
     * @param j0
     * @param i
     * @param j
     * @param offset
     * @return
     */
    static int[] cwNon0(int[] F, int w, int h, int i0, int j0, int i, int j, int offset) {
        int id = neighborIndexToID(i0, j0, i, j);
        for (int k = 0; k < N_PIXEL_NEIGHBOR; k++) {
            int kk = (-k + id - offset + N_PIXEL_NEIGHBOR * 2) % N_PIXEL_NEIGHBOR;
            int[] ij = neighborIDToIndex(i0, j0, kk);
            if (F[ij[0] * w + ij[1]] != 0) {
                return ij;
            }
        }
        return null;
    }

    /**
     * 每个坐标点
     */
    public class Point {
        public int x;
        public int y;

        public Point(int _x, int _y) {
            x = _x;
            y = _y;
        }

        public Point(Point p) {
            x = p.x;
            y = p.y;
        }
    }

    /**
     * 找到的轮廓
     */
    public class Contour {
        /**
         * 数据点
         */
        public ArrayList<Point> points;
        /**
         * 轮廓id，从2开始
         */
        public int id;
        /**
         * 父级id，0代表顶层
         */
        public int parent;
        /**
         * 轮廓是否是个洞
         */
        public boolean isHole;
    }

    /**
     * 根据二值化的图像找到轮廓
     * 根据Suzuki,S and Abe,K 1985年的论文Topological Structural Analysis of Digitized Binary Images by Border Following
     * (https://www.nevis.columbia.edu/~vgenty/public/suzuki_et_al.pdf) 实现
     * https://blog.csdn.net/u013631121/article/details/80504032?utm_source=blogxgwz4
     * https://www.cnblogs.com/liutianrui1/articles/10281465.html
     * https://blog.csdn.net/chchzh/article/details/109535132
     * https://zhuanlan.zhihu.com/p/397588540
     *
     * @param F 一维行列式二值图像数据，0代表背景，1代表前景，会被该函数修改
     * @param w 宽
     * @param h 高
     * @return 一系列轮廓
     * @see Contour
     */
    public ArrayList<Contour> findContours(int[] F, int w, int h) {
        int nbd = 1;//当前边界的数字标号
        int lnbd = 1;//上一个边界的数字标号

        ArrayList<Contour> contours = new ArrayList<Contour>();
        // 为了保证通用性，我们保证二值图像的最外面一圈是0
        // Nbd（number of border)一幅二值图的最上一行，最下一行，最左一行和最右一行都被设置为0，
        // 因此，这幅图像的最外层就是一个 hole, 这个hole称之为 背景(background)，
        // 而这最边上的两行两列组成的轮廓就是 一个孔边界，称之为frame.
        // 这个 frame 就用1 来标注。其次其他边界从2开始标注。
        for (int i = 1; i < h - 1; i++) {
            F[i * w] = 0;
            F[i * w + w - 1] = 0;
        }
        for (int i = 0; i < w; i++) {
            F[i] = 0;
            F[w * h - 1 - i] = 0;
        }
        //每个像素遍历一遍
        for (int i = 1; i < h - 1; i++) {
            lnbd = 1;//新一行重置上一个边界的标号
            for (int j = 1; j < w - 1; j++) {

                int i2 = 0, j2 = 0;
                if (F[i * w + j] == 0) {//数据是0代表背景，不处理直接跳到下一个
                    continue;
                }
                if (F[i * w + j] == 1 && F[i * w + (j - 1)] == 0) {//外边界的起始条件
                    nbd++;//当前边界加1
                    i2 = i;//记录外边界坐标
                    j2 = j - 1;
                } else if (F[i * w + j] >= 1 && F[i * w + j + 1] == 0) {//孔边界起始条件
                    nbd++;//当前边界加1
                    i2 = i;//记录孔边界坐标
                    j2 = j + 1;
                    if (F[i * w + j] > 1) {//已经查找过
                        lnbd = F[i * w + j];//
                    }
                } else {
                    //不是边界 直接跳到下一个点
                    if (F[i * w + j] != 1) {
                        lnbd = Math.abs(F[i * w + j]);
                    }
                    continue;

                }
                //(2)
                // 利用下表查找当前边界的父边界
                // ----------------------------------------------------------------
                // Type of border B'
                // \    with the sequential
                //     \     number LNBD
                // Type of B \                Outer border         Hole border
                // ---------------------------------------------------------------
                // Outer border               The parent border    The border B'
                //                            of the border B'
                //
                // Hole border                The border B'      The parent border
                //                                               of the border B'
                // ----------------------------------------------------------------

                Contour B = new Contour();
                B.points = new ArrayList<Point>();
                B.points.add(new Point(j, i));
                B.isHole = (j2 == j + 1);
                B.id = nbd;
                contours.add(B);

                Contour B0 = new Contour();
                for (int c = 0; c < contours.size(); c++) {
                    if (contours.get(c).id == lnbd) {
                        B0 = contours.get(c);
                        break;
                    }
                }
                //根据新找到的边界和上一个边界确定新找的的边界的父边界
                if (B0.isHole) {
                    if (B.isHole) {
                        B.parent = B0.parent;
                    } else {
                        B.parent = lnbd;
                    }
                } else {
                    if (B.isHole) {
                        B.parent = lnbd;
                    } else {
                        B.parent = B0.parent;
                    }
                }

                int i1 = -1, j1 = -1;
                int[] i1j1 = cwNon0(F, w, h, i, j, i2, j2, 0);//顺时针找到边界的最后一个点
                if (i1j1 == null) {//没找到非零元素
                    F[i * w + j] = -nbd;
                    if (F[i * w + j] != 1) {
                        lnbd = Math.abs(F[i * w + j]);
                    }
                    continue;
                }
                i1 = i1j1[0];
                j1 = i1j1[1];

                i2 = i1;//代表当前点的上一个找到的点
                j2 = j1;
                int i3 = i;//(i3,j3)代表当前点
                int j3 = j;


                while (true) {

                    int[] i4j4 = ccwNon0(F, w, h, i3, j3, i2, j2, 1);//逆时针查找(i4, j4)表示在当前点的邻域中查找到的最新的非零点
                    int i4 = i4j4[0];
                    int j4 = i4j4[1];

                    contours.get(contours.size() - 1).points.add(new Point(j4, i4));


                    if (F[i3 * w + j3 + 1] == 0) {//孔边界的最左侧的点和外边界的最右侧的点
                        //如果(i3,j3)前面的点(i3,j3+1)为0的话，那么我们把(i3,j3)这个点的像素值赋予-NBD。
                        F[i3 * w + j3] = -nbd;//即达到了最右侧，也就是碰到两个像素点为10的情况，-NBD设置为负数，防止outer的边界作为hole的起始点

                    } else if (F[i3 * w + j3] == 1) {//如果(i3,j3)点前面的点不为0，且(i3,j3)这个点的像素为1的话，那么把(i3,j3)的像素值赋予NBD。
                        F[i3 * w + j3] = nbd;
                    } else {

                    }
                    if (i4 == i && j4 == j && i3 == i1 && j3 == j1) {//轮廓跟踪了一圈，回到了开始点
                        if (F[i * w + j] != 1) {
                            lnbd = Math.abs(F[i * w + j]);
                        }
                        break;
                    } else {//跟踪下一个点
                        i2 = i3;
                        j2 = j3;
                        i3 = i4;
                        j3 = j4;
                    }
                }
            }
        }
        return contours;
    }

    /**
     * 找到点p到线段p0-p1的最短距离
     *
     * @param p  点p
     * @param p0 线段开始点
     * @param p1 线段结束点
     * @return 距离
     */
    float pointDistanceToSegment(Point p, Point p0, Point p1) {
        // https://stackoverflow.com/a/6853926
        // https://www.cnblogs.com/lyggqm/p/4651979.html
        // 利用点积可以知道 点p相对于p0-p1的角度
        // 如果是小于0，就是在p0的一边，最短距离就是pp0
        // 如果是大于1，就是在p1的一边，最短距离就是pp1
        // 如果在0-1，就是在p0和p1之间，最短距离就是 p到p0-p1的投影距离，而dot/len_sq代表的就是投影比例，计算一下得到投影距离
        float x = p.x;
        float y = p.y;
        float x1 = p0.x;
        float y1 = p0.y;
        float x2 = p1.x;
        float y2 = p1.y;
        float A = x - x1;
        float B = y - y1;
        float C = x2 - x1;
        float D = y2 - y1;
        float dot = A * C + B * D;
        float len_sq = C * C + D * D;
        float param = -1;
        if (len_sq != 0) {
            param = dot / len_sq;
        }
        float xx;
        float yy;
        if (param < 0) {// 3种角度情况
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }
        float dx = x - xx;
        float dy = y - yy;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 去除相邻点距离小于等于epsilon的点来简化轮廓
     *
     * @param polyline 轮廓点
     * @return
     */
    public ArrayList<Point> approxPolySimple(ArrayList<Point> polyline) {
        float epsilon = 0.1f;
        if (polyline.size() <= 2) {
            return polyline;
        }
        ArrayList<Point> ret = new ArrayList<Point>();
        ret.add(new Point(polyline.get(0)));

        for (int i = 1; i < polyline.size() - 1; i++) {
            float d = pointDistanceToSegment(polyline.get(i),
                    polyline.get(i - 1),
                    polyline.get(i + 1));
            if (d > epsilon) {
                ret.add(new Point(polyline.get(i)));
            }
        }
        ret.add(new Point(polyline.get(polyline.size() - 1)));
        return ret;
    }

    /**
     * 用Douglas–Peucker algorithm 简化轮廓
     * 根据DAVID H DOUGLAS and THOMAS K PEUCKER 1973年的论文Algorithms for the reduction of the number of points required to represent a digitized line or its caricature)
     * (http://www2.ipcku.kansai-u.ac.jp/~yasumuro/M_InfoMedia/paper/Douglas73.pdf) 实现
     * https://zhuanlan.zhihu.com/p/74906781
     *
     * @param polyline 轮廓点
     * @param epsilon  Maximum allowed error
     * @return
     */
    public ArrayList<Point> approxPolyDP(ArrayList<Point> polyline, float epsilon) {
        if (polyline.size() <= 2) {
            return polyline;
        }
        //将待处理曲线的首末点虚连一条直线,求所有中间点与直线的距离,并找出最大距离值dmax ,用dmax与抽稀阈值threshold相比较：
        //若dmax < threshold，这条曲线上的中间点全部舍去;
        //若dmax ≥ threshold，则以该点为界，把曲线分为两部分,对这两部分曲线重复上述过程，直至所有的点都被处理完成。
        float dmax = 0;
        int argmax = -1;
        for (int i = 1; i < polyline.size() - 1; i++) {
            float d = pointDistanceToSegment(polyline.get(i),
                    polyline.get(0),
                    polyline.get(polyline.size() - 1));
            if (d > dmax) {
                dmax = d;
                argmax = i;
            }
        }
        ArrayList<Point> ret = new ArrayList<Point>();
        if (dmax > epsilon) {
            ArrayList<Point> L = approxPolyDP(new ArrayList<Point>(polyline.subList(0, argmax + 1)), epsilon);
            ArrayList<Point> R = approxPolyDP(new ArrayList<Point>(polyline.subList(argmax, polyline.size())), epsilon);
            ret.addAll(L.subList(0, L.size() - 1));
            ret.addAll(R);
        } else {
            ret.add(new Point(polyline.get(0)));
            ret.add(new Point(polyline.get(polyline.size() - 1)));
        }
        return ret;
    }
}
