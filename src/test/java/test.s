	.text
	.globl	main
main:
	li t0,1
	addi sp,sp,4
	sw t0, 0(sp)
	li t0,0
	addi sp,sp,4
	sw t0, 0(sp)
	lw t1, 0(sp)
	addi sp,sp,-4
	lw t0, 0(sp)
	addi sp,sp,-4
	or t0,t0,t1
	snez t0,t0
	addi sp,sp,4
	sw t0, 0(sp)
	lw a0, 0(sp)
	addi sp,sp,-4
	ret
