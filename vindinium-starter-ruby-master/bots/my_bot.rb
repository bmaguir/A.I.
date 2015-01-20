
class MyBot < BaseBot

  def move state
    game = Game.new state
	#next_mine state, game.heroes_locs, game.mines_locs
	
	threads = []
	
	hero_no = "1"
	mp = state['game']['board']['tiles']
	sz = state['game']['board']['size']
	sz = sz*2
	new_map = ""
	(0..mp.length - 1).step(sz).each do |i|
		new_map << mp[i..i+(sz-1)].to_s + "\n"
	end
	my_pos = [ game.heroes_locs[hero_no][1], game.heroes_locs[hero_no][0]]
	game.mines_locs.each do |key, value|
		mine_pos = [key[1],key[0]]
		#puts my_pos
		#mine_pos = [0,4]
		threads<< Thread.new{thread_find_paths(new_map, sz, my_pos, mine_pos)}
	end
	
	threads.each do |t|
		t.join
	end
	
    DIRECTIONS.sample
  end

  def next_mine state, heroes, mines
	mp = state['game']['board']['tiles']
	sz = state['game']['board']['size']
	sz = sz*2
	new_map = ""
	(0..mp.length - 1).step(sz).each do |i|
		new_map << mp[i..i+(sz-1)].to_s + "\n"
	end
	y = 4
	x = 0
	new_map[y*(sz+1)+x] = "X"
	new_map[y*(sz+1)+x+1] = "X"
	puts "------------------\n" + new_map + "\n-------------------"
	
#=begin
	mines.each do |key, value|
		m = TileMap::Map.new(new_map,[ heroes["1"][1], heroes["1"][0]], [key[1],key[0]]) 	
		results = TileMap.a_star_search(m)
		t = m.get_tiles
		for i in (0 .. t.length-1)
			for j in (0 .. t[i].length)
				if t[i][j] == nil
					print "#"
				else
					for p in (0 .. results.length-1)
						if results[p][0].to_i == j && results[p][1].to_i == i
							t[i][j] = 8
						end
					end
					print t[i][j]
				end
			end
			puts
		end
		puts
	end
#=end

  end
  
  def thread_find_paths map, sz, my_pos, mine_pos 
	x = mine_pos[0] 
	y = mine_pos[1]
	map[y*(sz+1)+x*2] = "X"
	map[y*(sz+1)+x*2+1] = "X"
  
	#puts "------------------\n" + map + "\n-------------------"
  
	m = TileMap::Map.new(map, my_pos, mine_pos) 	
	
	results = TileMap.a_star_search(m)
	
	t = m.get_tiles
		for i in (0 .. t.length-1)
			for j in (0 .. t[i].length)
				if t[i][j] == nil
					print "#"
				else
					for p in (0 .. results.length-1)
						if results[p][0].to_i == j && results[p][1].to_i == i
							t[i][j] = 8
						end
					end
					print t[i][j]
				end
			end
			puts
		end
		puts
	
	return results
  
  end
  
  
end