package jp.co.internous.ecsite.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jp.co.internous.ecsite.model.dao.GoodsRepository;
import jp.co.internous.ecsite.model.dao.PurchaseRepository;
import jp.co.internous.ecsite.model.dao.UserRepository;
import jp.co.internous.ecsite.model.dto.HistoryDto;
import jp.co.internous.ecsite.model.dto.LoginDto;
import jp.co.internous.ecsite.model.entity.Goods;
import jp.co.internous.ecsite.model.entity.Purchase;
import jp.co.internous.ecsite.model.entity.User;
import jp.co.internous.ecsite.model.form.CartForm;
import jp.co.internous.ecsite.model.form.HistoryForm;
import jp.co.internous.ecsite.model.form.LoginForm;

@Controller //SpringBootにこのクラスはコントローラーであると認識させる。
@RequestMapping("/ecsite")
public class IndexController {
	//UserエンティティからuserテーブルにアクセスするDAO
	@Autowired //他のクラスを呼び出す。
	private UserRepository userRepos;
	//GoodsエンティティからgoodsテーブルにアクセスするためのDAO
	@Autowired
	private GoodsRepository goodsRepos;
	
	@Autowired
	private PurchaseRepository purchaseRepos;
	
	//webサービスAPIとして作成するためJSON形式を扱えるようGsonをインスタンス化
	private Gson gson = new Gson();
	
	//トップページ(index.html)に遷移するメソッド。goodsテーブルから取得した商品エンティティの一覧を、フロントに渡すModelに追加
	@RequestMapping("/") //SpringBootで作成したプロジェクトはデフォルトでtomCatを内包。　デフォルトであるhttp://localhost8080にアクセスした場合メソッドを実行
	public String index(Model m) {
		List<Goods> goods = goodsRepos.findAll();
		
		//画面に値を渡すために、Model modelを設定
		//model.addattributeメソッド
		m.addAttribute("goods",goods);  //【構文】model.addAttribute("属性名","渡したいデータ");　goodsというキーでgoodsReposを
		
		return "index";
	}
	
	@ResponseBody //return文で返却される文字列そのものが、レスポンスの本体(body)であることを示すアノテーション
	@PostMapping("/api/login") //画面からPOST通信で送られてきた際の処理　引数にはindex.htmlのformで設定したaction属性のパスを設定する。
	public String loginApi(@RequestBody LoginForm form) {
		
		//UserRepositoryのインスタンスから
		List<User> users = userRepos.findByUserNameAndPassword(form.getUserName(), form.getPassword()); 
		
		LoginDto dto = new LoginDto(0,null,null,"ゲスト");
		if(users.size()>0) {
			dto = new LoginDto(users.get(0));
		}
		return gson.toJson(dto);
	}

	@ResponseBody
	@PostMapping("/api/purchase")
	public String purchaseApi(@RequestBody CartForm f) {
		f.getCartList().forEach((c) ->{
			long total = c.getPrice() * c.getCount();
			purchaseRepos.persist(f.getUserId(),c.getId(),c.getGoodsName(),c.getCount(),total);
		});
		return String.valueOf(f.getCartList().size());
	}
	
	@ResponseBody
	@PostMapping("/api/history")
	public String historyApi(@RequestBody HistoryForm form) {
		String userId = form.getUserId();
		List<Purchase> history = purchaseRepos.findHistory(Long.parseLong(userId));
		List<HistoryDto> historyDtoList = new ArrayList<>(); //List<型名>変数名　=new ArrayList<>()
		history.forEach((v) ->{
			HistoryDto dto = new HistoryDto(v);
			historyDtoList.add(dto);
		});
		return gson.toJson(historyDtoList);
	}
	
}
