package com.bjpowernode.p2p.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName:BidUser
 * Package:com.bjpowernode.p2p.model.vo
 * Description
 *
 * @Date:2020/3/1821:27
 * @author:xyh
 */
@Data
public class BidUser implements Serializable {
    private String phone;
    private Double score;
}
