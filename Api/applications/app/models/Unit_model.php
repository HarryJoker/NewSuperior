<?php

class Unit_model extends CA_Model {

	private $statusScores = array(
			'11' => '0.5',
			'50' => '0.5',
			'52' => '0.5',
			'72' => '0.5',
			'74' => '0.5'
			// '100' => '1'
		);
     function __construct ()
     {/*{{{*/
         parent::__construct();
     }/*}}}*/


    public function getLeaderUnits()
	{
		return $this->get_where(array('role <'=> '3'));
	}


	public function getLeaderUnit()
	{
		$units = $this->get_where(array('role'=> '1'));
		if ($units && count($units)) return $units[0];
		return array();
	}

	public function getDeputyLeaderUnits()
	{
		return $this->get_where(array('role'=> '2'));
	}

	public function getDeputyUnits()
	{
		return $this->get_where(array('role'=> '4'));
	}


	public function getAllUnits() {
		return $this->get_where(array('role >' => 0));
	}


	public function getUnit($unitId) {
		return $this->get($unitId);
	}

	public function getUnitWithUsers($unitId) {
		$this->load->model('user_model');
		$unit = $this->get($unitId);
		$users = $this->user_model->getUnitUsers($unitId);
		$unit['users'] = $users;
		return $unit;
	}


	public function getAllUnitWithUser() {
		$units = $this->get_where_with_order(array('role >' => 0));
		$this->load->model('user_model');
		for($n = 0; $n < count($units); $n++) {
			$units[$n]['users'] = $this->user_model->getUnitUsers($units[$n]['id']);
		}
		return $units;
	}


	public function getAllUnitsWithAlis() {
		$sql = 'select id as unitLocalId, name as unitName, logo as unitLogo from tbl_unit'; 
		$result = $this->query($sql);
		return $reuslt;
	}


	//部门工作类型对应的综合得分
	public function getUnitScore($unitId = 0) {
		$categorys = array(1, 2, 3, 4, 5, 9);
		$unitCategoryScores = array();
		foreach ($categorys as $category) {
			$categoryScores = $this->getUnitScoreByCategory($category, $unitId);
			$unitCategoryScores[$category] = $this->makeCategorySynthesis($categoryScores);
		}
		return $unitCategoryScores;
	}

	//计算一个任务类型的综合得分
	private function makeCategorySynthesis($categoryScores = array()) {
		$synthesis = array_sum($categoryScores) / count($categoryScores);
		return array('synthesis' => $synthesis, 'statusScores' => $categoryScores);
	}


	//获取一个单位对应任务类型的得分
	public function getUnitScoreByCategory($category = 0, $unitId = 0) {
		$statusScoreSql = $this->makeStatusScoreSql($category, $unitId);
		$result = $this->makeUnitCategoryScoreBySql($statusScoreSql);
		return $result;
	}

	private function makeUnitCategoryScoreBySql($sql = '') {
		$result = $this->query($sql);
		$result = $this->makeMetaStatusScores($result);
		$newResult = array();
		foreach ($this->statusScores as $key => $value) {
			if (array_key_exists($key, $result)) {
				$newResult[$key] = 100 - $result[$key];
			} else {
				$newResult[$key] = 100;
			}
		}
		return $newResult;
	}

	private function makeStatusScoreSql($category = 0, $unitId = 0) {
		$unitTaskIdsSql = 'select ut.id from tbl_unittask ut left join tbl_task t on t.id = ut.taskId where category = '.$category.' and ut.unitId = '.$unitId;
		$whenSql = '';
		$whereSql = '';
		foreach ($this->statusScores as $status => $score) {
			$whenSql .= ' when status ='.$status.' then '.$score;
			$whereSql .= strlen($whereSql) > 0 ? ' or ' : '';
			$whereSql .= 'status = '.$status;
		}

		$sql = 'select id, unitTaskId, unitId, status, case '.$whenSql.' else 0 end score from tbl_trace where unitTaskId in ('.$unitTaskIdsSql.') and ('. $whereSql.')' ;
		$sql = 'select status, sum(score) as score from ('.$sql.') as tmp group by status';
		return $sql;
	}

	private function makeMetaStatusScores($scores = array()) {
		$newScores = array();
		foreach ($scores as $value) {
			$newScores[$value['status']] = $value['score'] * 1.0;
		}
		return $newScores;
	}

	function getPlatformUnitCount() {
		$sql = 'select count(*) as unitCount from tbl_unit';
		$result = $this->queryRow($sql);
		return $result;
	}

 }
 ?>
